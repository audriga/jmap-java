package com.audriga.jmap.gson.adapter;

import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.time.Duration;

public final class DurationAsMillisAdapter extends TypeAdapter<Duration> {
    public static void register(final GsonBuilder builder) {
        builder.registerTypeAdapter(Duration.class, new DurationAsMillisAdapter().nullSafe());
    }

    @Override
    public void write(JsonWriter out, Duration value) throws IOException {
        out.value(value.toMillis());
    }

    @Override
    public Duration read(JsonReader in) throws IOException {
        return Duration.ofMillis(in.nextLong());
    }
}
