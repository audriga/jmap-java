package rs.ltt.jmap.gson;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import rs.ltt.jmap.annotation.Default;
import rs.ltt.jmap.annotation.Type;

final class AtTypeSealedAdapterTest extends AbstractGsonTest {
    private static Gson gson;

    @BeforeAll
    static void init() {
        gson = getGson();
    }

    @Test
    void valid() throws IOException {
        var listType = new TypeToken<List<Parent>>() {};
        JsonElement json = parseFromResource("sealed/at-type-valid.json", JsonElement.class);
        var objects = List.of(
                new Parent.Gamma(List.of("a", "b", "c")), new Parent.Beta(1, (byte) -1), new Parent.Alpha("fdsa", "-"));
        assertEquals(objects, gson.fromJson(json, listType));
        // add default value to match serialization (we always write defaults)
        json.getAsJsonArray().get(2).getAsJsonObject().addProperty("another", "-");
        assertEquals(json, gson.toJsonTree(objects, listType.getType()));
    }

    @Test
    void invalid() throws IOException {
        var json = parseFromResource("sealed/at-type-invalid.json", new TypeToken<List<JsonElement>>() {});
        for (var elem : json) {
            assertThrows(JsonParseException.class, () -> gson.fromJson(elem, Parent.class));
        }
    }

    sealed interface Parent {
        @Type
        record Alpha(String a, @Default("\"-\"") String another) implements Parent {}

        @Type("second")
        record Beta(int b, byte bb) implements Parent {}

        @Type
        record Gamma(List<String> c) implements Parent {}
    }
}
