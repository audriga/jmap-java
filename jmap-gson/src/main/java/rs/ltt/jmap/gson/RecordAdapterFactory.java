package rs.ltt.jmap.gson;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
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
import java.util.stream.Collectors;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Default;
import rs.ltt.jmap.annotation.Inline;

public final class RecordAdapterFactory implements TypeAdapterFactory {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();
    private static final Object EMPTY_SLOT = new Object();

    private record Component(
            String name,
            Class<?> type,
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
                    var name = Annotations.get(c, SerializedName.class)
                            .map(SerializedName::value)
                            .orElse(c.getName());
                    @SuppressWarnings("unchecked")
                    var adapter = (TypeAdapter<Object>) gson.getAdapter(TypeToken.get(c.getGenericType()));
                    MethodHandle accessor;
                    try {
                        accessor = LOOKUP.unreflect(c.getAccessor());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    var inline = c.isAnnotationPresent(Inline.class);
                    var defaultJson = Annotations.get(c, Default.class)
                            .map(a -> gson.fromJson(a.value(), JsonElement.class))
                            .orElse(null);
                    var nullable = Annotations.getTypeUse(c, Nullable.class).isPresent();
                    if (nullable && c.getType().isPrimitive()) {
                        throw new IllegalStateException("record component " + raw.getName() + "." + c.getName()
                                + " is marked nullable but has primitive type");
                    }
                    return new Component(name, c.getType(), adapter, accessor, inline, defaultJson, nullable);
                })
                .toList();
        var componentNames = components.stream().map(Component::name).collect(Collectors.toUnmodifiableSet());
        if (componentNames.size() != components.size()) {
            throw new IllegalStateException(
                    "record " + raw.getName() + " has components with duplicate serialized name");
        }
        var nameToIndex = indexMap(components, Component::name);
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
            throw new IllegalStateException("failed to find primary record constructor for " + raw.getName(), e);
        }

        var jsonElementAdapter = gson.getAdapter(JsonElement.class);

        class NameWriter {
            private final JsonWriter out;
            private final Set<String> names;

            NameWriter(JsonWriter out, Set<String> names) {
                this.out = out;
                this.names = new HashSet<>(names);
            }

            void name(String name) throws IOException {
                if (!names.add(name)) {
                    throw new IllegalStateException("encountered duplicate name " + name);
                }
                out.name(name);
            }
        }

        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                out.beginObject();
                var nameWriter = new NameWriter(out, componentNames);
                for (var component : components) {
                    if (component.inline) {
                        var tree = component
                                .adapter
                                .toJsonTree(GsonUtils.invoke(component.accessor))
                                .getAsJsonObject();
                        for (var entry : tree.entrySet()) {
                            nameWriter.name(entry.getKey());
                            jsonElementAdapter.write(out, entry.getValue());
                        }
                    } else {
                        nameWriter.name(component.name);
                        component.adapter.write(out, GsonUtils.invoke(component.accessor));
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
                    var it = tree.entrySet().iterator();
                    while (it.hasNext()) {
                        var entry = it.next();
                        var index = nameToIndex.get(entry.getKey());
                        if (index == null) continue;
                        var comp = components.get(index);
                        if (comp.inline) continue;
                        if (fields[index] != EMPTY_SLOT) {
                            throw new JsonParseException("encountered duplicate name '" + entry.getKey() + "'");
                        }
                        fields[index] = comp.adapter.fromJsonTree(entry.getValue());
                        // processed, remove from tree
                        it.remove();
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
                        if (index == null) continue;
                        if (fields[index] != EMPTY_SLOT) {
                            throw new JsonParseException("encountered duplicate name '" + name + "'");
                        }
                        fields[index] = components.get(index).adapter.read(in);
                    }
                    in.endObject();
                }
                for (int i = 0; i < fields.length; ++i) {
                    if (fields[i] == EMPTY_SLOT) {
                        var comp = components.get(i);
                        if (comp.defaultJson != null) {
                            fields[i] = comp.adapter.fromJsonTree(comp.defaultJson);
                        } else if (comp.nullable) {
                            // we disallow nullable on primitive types, so this is safe
                            fields[i] = null;
                        } else {
                            throw new JsonParseException("missing required field " + comp.name);
                        }
                        if (fields[i] == null && !comp.nullable) {
                            throw new JsonParseException("found null value for non-nullable property '" + comp.name()
                                    + "' while parsing " + raw.getName());
                        }
                    }
                }
                @SuppressWarnings("unchecked")
                T result = (T) GsonUtils.invoke(ctor, fields);
                return result;
            }
        }.nullSafe();
    }

    private static <A, B> Map<B, Integer> indexMap(List<A> list, Function<A, B> makeKey) {
        var res = new HashMap<B, Integer>();
        int i = 0;
        for (var a : list) {
            res.put(makeKey.apply(a), i);
            ++i;
        }
        return Map.copyOf(res);
    }
}
