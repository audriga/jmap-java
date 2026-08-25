package com.audriga.jmap.gson.factory;

import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.common.method.ResultReference;
import com.audriga.jmap.gson.NameValueTypeAdapter;
import com.google.common.collect.ImmutableSet;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Set;

public final class MethodCallArgAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!MethodCall.Arg.class.isAssignableFrom(type.getRawType())) return null;
        @SuppressWarnings("unchecked")
        var valueAdapter = (TypeAdapter<Object>)
                gson.getAdapter(TypeToken.get(((ParameterizedType) type.getType()).getActualTypeArguments()[0]));
        var referenceAdapter = gson.getAdapter(ResultReference.class);

        return new NameValueTypeAdapter<>() {
            @Override
            public Instance<T> instantiate(String name, Set<String> alternateNames) {
                var namesBuilder = ImmutableSet.<String>builder()
                        .add(name)
                        .addAll(alternateNames)
                        .add("#" + name);
                for (var n : alternateNames) {
                    namesBuilder.add("#" + n);
                }
                var names = namesBuilder.build();

                return new Instance<>() {
                    @Override
                    public Set<String> names() {
                        return names;
                    }

                    @Override
                    public void write(JsonWriter out, NameWriter nameWriter, T value) throws IOException {
                        if (value instanceof MethodCall.Arg.Value<?> v) {
                            nameWriter.write(name);
                            valueAdapter.write(out, v.value());
                        } else if (value instanceof MethodCall.Arg.Reference<?> r) {
                            nameWriter.write("#" + name);
                            referenceAdapter.write(out, r.reference());
                        } else {
                            nameWriter.write(name);
                            out.nullValue();
                        }
                    }

                    @Override
                    public T read(JsonReader in, String name) throws IOException {
                        MethodCall.Arg<Object> res;
                        if (name.startsWith("#")) {
                            res = MethodCall.Arg.of(referenceAdapter.read(in));
                        } else {
                            res = MethodCall.Arg.of(valueAdapter.read(in));
                        }
                        @SuppressWarnings("unchecked")
                        var cast = (T) res;
                        return cast;
                    }
                };
            }
        };
    }
}
