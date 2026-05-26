package rs.ltt.jmap.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public final class SetAsObjectAdapterFactory implements TypeAdapterFactory {
    @Override
    @SuppressWarnings("unchecked")
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        var raw = type.getRawType();
        // only support Set and no subtypes for simplicity of construction
        if (raw != Set.class) return null;
        var elementType = type.getType() instanceof ParameterizedType p ? p.getActualTypeArguments()[0] : Object.class;
        var elementAdapter = (TypeAdapter<Object>) gson.getAdapter(TypeToken.get(elementType));

        return (TypeAdapter<T>)
                new TypeAdapter<Set<Object>>() {
                    @Override
                    public void write(JsonWriter out, Set<Object> value) throws IOException {
                        out.beginObject();
                        for (var o : value) {
                            var key = elementAdapter.toJsonTree(o);
                            if (key instanceof JsonPrimitive primitive && primitive.isString()) {
                                out.name(primitive.getAsString());
                            } else {
                                throw new IllegalArgumentException(
                                        "value '" + o + "' serialized to " + key + ", expected a JSON string");
                            }
                            out.value(true);
                        }
                        out.endObject();
                    }

                    @Override
                    public Set<Object> read(JsonReader in) throws IOException {
                        var result = new LinkedHashSet<>();
                        in.beginObject();
                        while (in.peek() != JsonToken.END_OBJECT) {
                            var name = new JsonPrimitive(in.nextName());
                            var element = elementAdapter.fromJsonTree(name);
                            if (!in.nextBoolean()) {
                                throw new JsonParseException("All keys of a set object must be true");
                            }
                            result.add(element);
                        }
                        in.endObject();
                        return Collections.unmodifiableSet(result);
                    }
                }.nullSafe();
    }
}
