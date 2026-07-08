package com.audriga.jmap.gson.adapter;

import com.audriga.jmap.common.DateTimePeriod;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;

public final class DateTimePeriodTypeAdapter extends TypeAdapter<DateTimePeriod> {
    public static void register(final GsonBuilder builder) {
        builder.registerTypeAdapter(DateTimePeriod.class, new DateTimePeriodTypeAdapter().nullSafe());
    }

    @Override
    public void write(JsonWriter out, DateTimePeriod value) throws IOException {
        out.value(value.toString());
    }

    @Override
    public DateTimePeriod read(JsonReader in) throws IOException {
        return DateTimePeriod.parse(in.nextString());
    }
}
