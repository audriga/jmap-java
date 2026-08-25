package com.audriga.jmap.gson.factory;

import com.audriga.jmap.annotation.Default;
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
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.RecordComponent;
import java.util.*;
import java.util.function.Function;
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
            Class<?> type,
            NameValueTypeAdapter.Instance<Object> nvAdapter,
            TypeAdapter<Object> adapter,
            MethodHandle accessor,
            boolean inline,
            @Nullable JsonElement defaultJson,
            boolean nullable) {}

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        var raw = type.getRawType();
        var componentArray = raw.getRecordComponents();
        if (componentArray == null) return null;
        // we don't want to deal with recursive records
        if (Arrays.stream(componentArray).anyMatch(c -> c.getType().equals(raw))) return null;

        var components = Arrays.stream(componentArray)
                .map(c -> {
                    var inline = c.isAnnotationPresent(Inline.class);
                    var serializedName = Annotations.get(c, SerializedName.class);
                    @SuppressWarnings("unchecked")
                    var adapter = (TypeAdapter<Object>) gson.getAdapter(TypeToken.get(c.getGenericType()));
                    NameValueTypeAdapter.Instance<Object> nvAdapter;
                    if (inline) {
                        nvAdapter = null;
                        if (serializedName.isPresent()) {
                            throw new IllegalArgumentException(
                                    "useless @SerializedName on @Inline record component " + raw + "." + c.getName());
                        }
                    } else {
                        var name = serializedName.map(SerializedName::value).orElse(c.getName());
                        var alternateNames = serializedName
                                .map(SerializedName::alternate)
                                .map(Set::of)
                                .orElse(Set.of());
                        if (adapter instanceof NameValueTypeAdapter<Object> nv) {
                            nvAdapter = nv.instantiate(name, alternateNames);
                        } else {
                            var names = ImmutableSet.<String>builder()
                                    .add(name)
                                    .addAll(alternateNames)
                                    .build();
                            nvAdapter = new NameValueTypeAdapter.Instance<>() {
                                @Override
                                public Set<String> names() {
                                    return names;
                                }

                                @Override
                                public void write(
                                        JsonWriter out, NameValueTypeAdapter.NameWriter nameWriter, Object value)
                                        throws IOException {
                                    nameWriter.write(name);
                                    adapter.write(out, value);
                                }

                                @Override
                                public Object read(JsonReader in, String name) throws IOException {
                                    return adapter.read(in);
                                }
                            };
                        }
                    }

                    MethodHandle accessor;
                    try {
                        accessor = LOOKUP.unreflect(c.getAccessor());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    var defaultJson = Annotations.get(c, Default.class)
                            .map(a -> gson.fromJson(a.value(), JsonElement.class))
                            .orElse(null);
                    if (defaultJson != null && inline) {
                        throw new IllegalArgumentException("record component " + raw + "." + c.getName()
                                + " cannot be marked @Inline and have a @Default value at the same time");
                    }
                    var nullable = Annotations.get(c, Nullable.class).isPresent();
                    if (nullable && c.getType().isPrimitive()) {
                        throw new IllegalArgumentException("record component " + raw.getName() + "." + c.getName()
                                + " is marked nullable but has primitive type");
                    }
                    return new Component(c.getType(), nvAdapter, adapter, accessor, inline, defaultJson, nullable);
                })
                .toList();
        var nameToIndex = indexMap(components, c -> c.nvAdapter.names());
        var needsFlatten = components.stream().anyMatch(Component::inline);

        var ctorType = MethodType.methodType(
                void.class,
                Arrays.stream(componentArray)
                        .<Class<?>>map(RecordComponent::getType)
                        .toList());
        MethodHandle ctor;
        try {
            ctor = LOOKUP.findConstructor(raw, ctorType);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalArgumentException("failed to find primary record constructor for " + raw.getName(), e);
        }

        var jsonElementAdapter = gson.getAdapter(JsonElement.class);

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
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
            public T read(JsonReader in) throws IOException {
                var fields = new Object[components.size()];
                Arrays.fill(fields, EMPTY_SLOT);
                if (needsFlatten) {
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
                            fields[i] = ZERO_VALUES.get(comp.type);
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
                @SuppressWarnings("unchecked")
                T result = (T) GsonUtils.invoke(ctor, fields);
                return result;
            }
        }.nullSafe();
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
