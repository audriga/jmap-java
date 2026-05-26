package com.audriga.jmap.gson;

import com.audriga.jmap.common.entity.VendorExtension;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public final class VendorPropertyMapAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!Map.class.equals(type.getRawType())) return null;
        if (!(type.getType() instanceof ParameterizedType pType)) return null;
        var args = pType.getActualTypeArguments();
        if (!VendorExtension.class.equals(args[0])) return null;
        @SuppressWarnings("unchecked")
        var valueAdapter = (TypeAdapter<Object>) gson.getAdapter(TypeToken.get(args[1]));
        return new TypeAdapter<T>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                @SuppressWarnings("unchecked")
                var map = (Map<VendorExtension, Object>) value;
                out.beginObject();
                for (var entry : map.entrySet()) {
                    out.name(entry.getKey().toString());
                    valueAdapter.write(out, entry.getValue());
                }
                out.endObject();
            }

            @Override
            public T read(JsonReader in) throws IOException {
                var map = new LinkedHashMap<VendorExtension, Object>();
                in.beginObject();
                while (in.peek() != JsonToken.END_OBJECT) {
                    var prop = VendorExtension.parse(in.nextName());
                    if (prop == null) {
                        in.skipValue();
                        continue;
                    }
                    var value = valueAdapter.read(in);
                    var old = map.put(prop, value);
                    if (old != null) {
                        throw new JsonParseException(
                                "duplicate property '" + prop + "' with values '" + old + "' and '" + value + "'");
                    }
                }
                in.endObject();
                @SuppressWarnings("unchecked")
                var result = (T) Collections.unmodifiableMap(map);
                return result;
            }
        }.nullSafe();
    }
}
