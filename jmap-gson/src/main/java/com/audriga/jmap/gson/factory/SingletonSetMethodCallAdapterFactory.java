package com.audriga.jmap.gson.factory;

import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.common.method.call.singleton.SingletonSetMethodCall;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Map;

public final class SingletonSetMethodCallAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        if (!SingletonSetMethodCall.class.isAssignableFrom(type.getRawType())) return null;

        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        final TypeAdapter<Map<String, Object>> mapAdapter = gson.getAdapter(new TypeToken<Map<String, Object>>() {});

        return new TypeAdapter<>() {
            @Override
            public void write(JsonWriter out, T value) throws IOException {
                final var call = (SingletonSetMethodCall<?>) value;
                out.beginObject();

                out.name("accountId");
                out.value(MethodCall.Arg.unwrapValue(call.accountId()));

                final var ifInState = call.ifInState();
                if (ifInState != null) {
                    out.name("ifInState");
                    out.value(MethodCall.Arg.unwrapValue(ifInState));
                }

                final var single = call.updateSingle();
                if (single != null) {
                    out.name("update");
                    out.beginObject();
                    out.name("singleton");
                    mapAdapter.write(out, single);
                    out.endObject();
                }

                out.endObject();
            }

            @Override
            public T read(JsonReader in) throws IOException {
                return delegate.read(in);
            }
        };
    }
}
