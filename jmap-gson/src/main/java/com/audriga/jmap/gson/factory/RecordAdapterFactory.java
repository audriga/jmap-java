package com.audriga.jmap.gson.factory;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Deserializer;
import com.audriga.jmap.annotation.Inline;
import com.audriga.jmap.gson.Annotations;
import com.audriga.jmap.gson.GsonUtils;
import com.audriga.jmap.gson.NameValueTypeAdapter;
import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.bind.JsonTreeReader;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import org.jspecify.annotations.Nullable;

public final class RecordAdapterFactory implements TypeAdapterFactory {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final Object EMPTY_SLOT = new Object();
    // `get` will return null for non-primitive types
    private static final Map<Class<?>, Object> ZERO_VALUES = Map.of(
            byte.class,
            (byte) 0,
            short.class,
            (short) 0,
            int.class,
            0,
            long.class,
            0L,
            float.class,
            0f,
            double.class,
            0d,
            char.class,
            (char) 0,
            boolean.class,
            false);

    private record Component(
            NameValueTypeAdapter.Instance<Object> nvAdapter,
            TypeAdapter<Object> adapter,
            MethodHandle accessor,
            boolean inline,
            @Nullable JsonElement defaultJson,
            boolean nullable,
            Object zeroValue) {
        public interface AnnotationAccess {
            <A extends Annotation> Optional<A> get(Class<A> clazz);
        }

        public static Component reflect(
                Gson gson,
                Type type,
                Class<?> rawType,
                String name,
                AnnotationAccess annotations,
                MethodHandle accessor) {
            var inline = annotations.get(Inline.class).isPresent();
            var serializedName = annotations.get(SerializedName.class);
            @SuppressWarnings("unchecked")
            var adapter = (TypeAdapter<Object>) gson.getAdapter(TypeToken.get(type));
            NameValueTypeAdapter.Instance<Object> nvAdapter;
            if (inline) {
                nvAdapter = null;
                if (serializedName.isPresent()) {
                    throw new IllegalArgumentException("useless @SerializedName on @Inline component " + name);
                }
            } else {
                var primaryName = serializedName.map(SerializedName::value).orElse(name);
                var alternateNames = serializedName
                        .map(SerializedName::alternate)
                        .map(Set::of)
                        .orElse(Set.of());
                if (adapter instanceof NameValueTypeAdapter<Object> nv) {
                    nvAdapter = nv.instantiate(primaryName, alternateNames);
                } else {
                    var names = ImmutableSet.<String>builder()
                            .add(primaryName)
                            .addAll(alternateNames)
                            .build();
                    nvAdapter = new NameValueTypeAdapter.Instance<>() {
                        @Override
                        public Set<String> names() {
                            return names;
                        }

                        @Override
                        public void write(JsonWriter out, NameValueTypeAdapter.NameWriter nameWriter, Object value)
                                throws IOException {
                            nameWriter.write(primaryName);
                            adapter.write(out, value);
                        }

                        @Override
                        public Object read(JsonReader in, String name) throws IOException {
                            return adapter.read(in);
                        }
                    };
                }
            }

            var defaultJson = annotations
                    .get(Default.class)
                    .map(a -> gson.fromJson(a.value(), JsonElement.class))
                    .orElse(null);
            if (defaultJson != null && inline) {
                throw new IllegalArgumentException(
                        "component " + name + " cannot be marked @Inline and have an @Default value at the same time");
            }
            var nullable = annotations.get(Nullable.class).isPresent();
            if (nullable && rawType.isPrimitive()) {
                throw new IllegalArgumentException("component " + name + " is marked nullable but has primitive type");
            }
            return new Component(nvAdapter, adapter, accessor, inline, defaultJson, nullable, ZERO_VALUES.get(rawType));
        }
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        var raw = type.getRawType();

        List<Component> components;
        MethodHandle ctor;

        // use getDeclaredMethods to exclude static methods of superclasses
        var deserializers = Stream.concat(Arrays.stream(raw.getConstructors()), Arrays.stream(raw.getDeclaredMethods()))
                .filter(x -> {
                    if (!x.isAnnotationPresent(Deserializer.class)) return false;
                    if (x instanceof Method && !Modifier.isStatic(x.getModifiers())) {
                        throw new IllegalArgumentException(
                                "non-static method " + x + " must not be annotated with @Deserializer");
                    }
                    if (!Modifier.isPublic(x.getModifiers())) {
                        throw new IllegalArgumentException("@Deserializer " + x + " is not public");
                    }
                    return true;
                })
                .toList();
        switch (deserializers.size()) {
            case 0 -> {
                var recordComponents = raw.getRecordComponents();
                if (recordComponents == null) return null;

                // we don't want to deal with recursive records
                if (Arrays.stream(recordComponents).anyMatch(c -> c.getType().equals(raw))) return null;

                components = Arrays.stream(recordComponents)
                        .map(c -> {
                            MethodHandle accessor;
                            try {
                                accessor = LOOKUP.unreflect(c.getAccessor());
                            } catch (IllegalAccessException e) {
                                throw new IllegalArgumentException(
                                        "cannot access record component accessor for " + c.getName(), e);
                            }
                            return Component.reflect(
                                    gson,
                                    c.getGenericType(),
                                    c.getType(),
                                    c.getName(),
                                    new Component.AnnotationAccess() {
                                        @Override
                                        public <A extends Annotation> Optional<A> get(Class<A> clazz) {
                                            return Annotations.get(c, clazz);
                                        }
                                    },
                                    accessor);
                        })
                        .toList();
                var ctorType = MethodType.methodType(
                        void.class,
                        Arrays.stream(recordComponents)
                                .<Class<?>>map(RecordComponent::getType)
                                .toList());
                try {
                    ctor = LOOKUP.findConstructor(raw, ctorType);
                } catch (IllegalAccessException | NoSuchMethodException e) {
                    throw new IllegalArgumentException(
                            "failed to find primary record constructor for " + raw.getName(), e);
                }
            }
            case 1 -> {
                var deserializer = deserializers.get(0);
                components = Arrays.stream(deserializer.getParameters())
                        .map(p -> {
                            Method accessorMethod;
                            MethodHandle accessor;
                            try {
                                accessorMethod = raw.getMethod(p.getName());
                                accessor = LOOKUP.unreflect(accessorMethod);
                            } catch (NoSuchMethodException e) {
                                throw new IllegalArgumentException(
                                        "cannot find accessor method for component " + p.getName(), e);
                            } catch (IllegalAccessException e) {
                                throw new IllegalArgumentException(
                                        "cannot access component accessor method for " + p.getName(), e);
                            }
                            return Component.reflect(
                                    gson,
                                    p.getParameterizedType(),
                                    p.getType(),
                                    p.getName(),
                                    new Component.AnnotationAccess() {
                                        @Override
                                        public <A extends Annotation> Optional<A> get(Class<A> clazz) {
                                            return Annotations.get(p, clazz)
                                                    .or(() -> Annotations.get(p.getAnnotatedType(), clazz))
                                                    .or(() -> Annotations.get(accessorMethod, clazz));
                                        }
                                    },
                                    accessor);
                        })
                        .toList();
                try {
                    if (deserializer instanceof Constructor<?> c) {
                        ctor = LOOKUP.unreflectConstructor(c);
                    } else if (deserializer instanceof Method m) {
                        ctor = LOOKUP.unreflect(m);
                    } else {
                        throw new AssertionError();
                    }
                } catch (IllegalAccessException e) {
                    throw new IllegalArgumentException("cannot access marked deserializer " + deserializer, e);
                }
            }
            default ->
                throw new IllegalArgumentException(
                        "found multiple @Deserializers in " + raw + "(" + deserializers + ")");
        }

        @SuppressWarnings("unchecked")
        var adapter = (TypeAdapter<T>) new Adapter(gson.getAdapter(JsonElement.class), components, ctor);
        return adapter.nullSafe();
    }

    private static final class Adapter extends TypeAdapter<Object> {
        private final TypeAdapter<JsonElement> jsonElementAdapter;
        private final List<Component> components;
        private final Map<String, Integer> nameToIndex;
        private final boolean needsInline;
        private final MethodHandle ctor;

        private Adapter(TypeAdapter<JsonElement> jsonElementAdapter, List<Component> components, MethodHandle ctor) {
            this.jsonElementAdapter = jsonElementAdapter;
            this.components = components;
            this.nameToIndex = indexMap(components, c -> c.nvAdapter != null ? c.nvAdapter.names() : Set.of());
            this.needsInline = components.stream().anyMatch(Component::inline);
            this.ctor = ctor;
        }

        @Override
        public void write(JsonWriter out, Object value) throws IOException {
            out.beginObject();
            var names = new HashSet<>();
            for (var component : components) {
                if (component.inline) {
                    var tree = component.adapter.toJsonTree(GsonUtils.invoke(component.accessor, value));
                    if (tree.isJsonNull()) continue;
                    for (var entry : tree.getAsJsonObject().entrySet()) {
                        var name = entry.getKey();
                        if (!names.add(name)) {
                            throw new IllegalArgumentException("encountered duplicate name " + name);
                        }
                        out.name(name);
                        jsonElementAdapter.write(out, entry.getValue());
                    }
                } else {
                    component.nvAdapter.write(
                            out,
                            s -> {
                                if (!names.add(s)) {
                                    throw new IllegalArgumentException("encountered duplicate name " + s);
                                }
                                out.name(s);
                            },
                            GsonUtils.invoke(component.accessor, value));
                }
            }
            out.endObject();
        }

        @Override
        public Object read(JsonReader in) throws IOException {
            var fields = new Object[components.size()];
            Arrays.fill(fields, EMPTY_SLOT);
            if (needsInline) {
                var tree = jsonElementAdapter.read(in).getAsJsonObject();
                for (var entry : tree.entrySet()) {
                    var index = nameToIndex.get(entry.getKey());
                    if (index == null) continue;
                    var comp = components.get(index);
                    if (comp.inline) continue;
                    if (fields[index] != EMPTY_SLOT) {
                        throw new JsonParseException("encountered duplicate name '" + entry.getKey() + "'");
                    }
                    var reader = new JsonTreeReader(entry.getValue());
                    fields[index] = comp.nvAdapter.read(reader, entry.getKey());
                }
                for (int i = 0; i < fields.length; ++i) {
                    var comp = components.get(i);
                    if (!comp.inline) continue;
                    fields[i] = comp.adapter.fromJsonTree(tree);
                }
            } else {
                in.beginObject();
                while (in.peek() != JsonToken.END_OBJECT) {
                    var name = in.nextName();
                    var index = nameToIndex.get(name);
                    if (index == null) {
                        in.skipValue();
                        continue;
                    }
                    if (fields[index] != EMPTY_SLOT) {
                        throw new JsonParseException("encountered duplicate name '" + name + "'");
                    }
                    fields[index] = components.get(index).nvAdapter.read(in, name);
                }
                in.endObject();
            }
            for (int i = 0; i < fields.length; ++i) {
                var comp = components.get(i);
                if (fields[i] == EMPTY_SLOT) {
                    if (comp.defaultJson != null) {
                        fields[i] = comp.adapter.fromJsonTree(comp.defaultJson);
                    } else if (comp.nullable) {
                        // we disallow nullable on primitive types, so this is safe
                        fields[i] = null;
                    } else {
                        fields[i] = comp.zeroValue;
                        // FIXME: in some contexts, all fields are considered nullable
                        //  (e.g. created/updated in SetMethodResponse)
                        // throw new JsonParseException("missing required field " + comp.name);
                    }
                }
                // FIXME: see above
                // if (fields[i] == null && !comp.nullable) {
                //    throw new JsonParseException("found null value for non-nullable property '" + comp.name()
                //            + "' while parsing " + raw.getName());
                // }
            }
            return GsonUtils.invoke(ctor, fields);
        }
    }

    private static <A, B> Map<B, Integer> indexMap(List<A> list, Function<A, Set<B>> makeKey) {
        var res = new HashMap<B, Integer>();
        int i = 0;
        for (var a : list) {
            for (var k : makeKey.apply(a)) {
                if (res.put(k, i) != null) {
                    throw new IllegalArgumentException("duplicate name '" + k + "'");
                }
            }
            ++i;
        }
        return Map.copyOf(res);
    }
}
