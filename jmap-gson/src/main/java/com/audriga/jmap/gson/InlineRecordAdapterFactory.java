package com.audriga.jmap.gson;

import com.audriga.jmap.annotation.Inline;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class InlineRecordAdapterFactory implements TypeAdapterFactory {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        var raw = type.getRawType();
        if (!raw.isAnnotationPresent(Inline.class)) return null;
        var components = raw.getRecordComponents();
        if (components == null) {
            throw new IllegalArgumentException("@Inline is only permitted on record classes, got " + raw.getName());
        } else if (components.length != 1) {
            throw new IllegalArgumentException(
                    "cannot inline record class " + raw.getName() + " because it doesn't have exactly one component");
        }
        var component = components[0];
        MethodHandle ctor;
        try {
            ctor = LOOKUP.findConstructor(raw, MethodType.methodType(void.class, component.getType()));
        } catch (IllegalAccessException | NoSuchMethodException e) {
            throw new IllegalArgumentException("failed to find primary record constructor for " + raw.getName(), e);
        }
        MethodHandle accessor;
        try {
            accessor = LOOKUP.unreflect(component.getAccessor());
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException("failed to unreflect record accessor for " + raw.getName(), e);
        }
        if (component.getType() == raw) {
            throw new IllegalArgumentException("cannot inline recursive record " + raw);
        }
        @SuppressWarnings("unchecked")
        var adapter = (TypeAdapter<Object>) gson.getAdapter(TypeToken.get(component.getGenericType()));

        return new TypeAdapter<>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    adapter.write(out, GsonUtils.invoke(accessor, value));
                }
            }

            @Override
            @SuppressWarnings("unchecked")
            public T read(JsonReader in) throws IOException {
                var read = adapter.read(in);
                return read == null ? null : (T) GsonUtils.invoke(ctor, read);
            }
        };
    }
}
