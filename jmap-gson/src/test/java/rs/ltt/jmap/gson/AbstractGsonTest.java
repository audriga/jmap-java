package rs.ltt.jmap.gson;

import com.google.common.io.Resources;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

abstract class AbstractGsonTest {

    protected static final Instant OCTOBER_FIRST_8AM = Instant.ofEpochSecond(1601539200);
    protected static final Instant OCTOBER_THIRD_8PM = Instant.ofEpochSecond(1601755200);

    static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        JmapAdapters.register(gsonBuilder);
        return gsonBuilder.create();
    }

    @SuppressWarnings("unchecked")
    static <T> T parseFromResource(String filename, Type type) throws IOException {
        return (T) parseFromResource(filename, TypeToken.get(type));
    }

    static <T> T parseFromResource(String filename, TypeToken<T> type) throws IOException {
        final Gson gson = getGson();
        return gson.fromJson(
                Resources.asCharSource(Resources.getResource(filename), StandardCharsets.UTF_8)
                        .read(),
                type);
    }

    public String readResourceAsString(String filename) throws IOException {
        return Resources.asCharSource(Resources.getResource(filename), StandardCharsets.UTF_8)
                .read()
                .trim();
    }
}
