package com.audriga.jmap.gson.adapter;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.ZoneId;

public final class ZoneIdTypeAdapter extends TypeAdapter<ZoneId> {
    public static void register(GsonBuilder builder) {
        builder.registerTypeAdapter(ZoneId.class, new ZoneIdTypeAdapter().nullSafe());
    }

    @Override
    public void write(JsonWriter out, ZoneId value) throws IOException {
        out.value(value.getId());
    }

    @Override
    public ZoneId read(JsonReader in) throws IOException {
        return ZoneId.of(in.nextString());
    }
}
