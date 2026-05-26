package com.audriga.jmap.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Predicate;

public final class OmitEmptyAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Predicate<T> isEmpty;
        if (Collection.class.isAssignableFrom(type.getRawType())) isEmpty = t -> ((Collection<?>) t).isEmpty();
        else if (Map.class.isAssignableFrom(type.getRawType())) isEmpty = t -> ((Map<?, ?>) t).isEmpty();
        else return null;
        var delegate = gson.getDelegateAdapter(this, type);
        return new TypeAdapter<>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                if (value == null || isEmpty.test(value)) {
                    out.nullValue();
                } else {
                    delegate.write(out, value);
                }
            }

            @Override
            public T read(JsonReader in) throws IOException {
                return delegate.read(in);
            }
        };
    }
}
