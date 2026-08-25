package com.audriga.jmap.gson;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.Set;

public abstract class NameValueTypeAdapter<T> extends TypeAdapter<T> {
    public abstract Instance<T> instantiate(String name, Set<String> alternateNames);

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        throw new UnsupportedOperationException("NameValueTypeAdapter needs to be instantiated first");
    }

    @Override
    public T read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException("NameValueTypeAdapter needs to be instantiated first");
    }

    public interface Instance<T> {
        Set<String> names();

        /**
         * {@code nameWriter} has to be invoked before writing the value to {@code out}.
         */
        void write(JsonWriter out, NameWriter nameWriter, T value) throws IOException;

        T read(JsonReader in, String name) throws IOException;
    }

    public interface NameWriter {
        void write(String name) throws IOException;
    }
}
