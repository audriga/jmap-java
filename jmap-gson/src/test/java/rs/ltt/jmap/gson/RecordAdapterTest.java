package rs.ltt.jmap.gson;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import java.io.IOException;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import rs.ltt.jmap.annotation.Default;
import rs.ltt.jmap.annotation.Inline;

final class RecordAdapterTest extends AbstractGsonTest {
    private static Gson gson;

    @BeforeAll
    static void init() {
        gson = getGson();
    }

    @Test
    void full() throws IOException {
        System.out.println(Foo.class.getRecordComponents()[1].getAccessor().getAnnotation(SerializedName.class));
        JsonElement json = parseFromResource("record/full.json", JsonElement.class);
        var object = new Foo(-12345, "a string!", true, new Bar(9.8), new Baz(2, new Bar(42)), "");
        assertEquals(json, gson.toJsonTree(object));
        assertEquals(object, gson.fromJson(json, Foo.class));

        @Inline
        record Wrapper(Foo foo) {}
        var wrapped = new Wrapper(object);
        assertEquals(json, gson.toJsonTree(wrapped));
        assertEquals(wrapped, gson.fromJson(json, Wrapper.class));
    }

    @Test
    void defaults() throws IOException {
        var object = new Foo(-12345, "a string!", true, new Bar(3.14), new Baz(1, new Bar(7)), null);
        var parsed = parseFromResource("record/defaults.json", Foo.class);
        assertEquals(object, parsed);
        assertEquals(object, gson.fromJson(gson.toJson(parsed), Foo.class));
    }

    @Test
    void nullablePrimitive() {
        record X(@Nullable int field) {}
        assertThrows(IllegalArgumentException.class, () -> gson.getAdapter(X.class));
    }

    @Test
    void defaultPlusInline() {
        record X() {}
        record Y(@Inline @Default("{}") X field) {}
        assertThrows(IllegalArgumentException.class, () -> gson.getAdapter(Y.class));
    }

    @Test
    void duplicatePropertyWrite() {
        record X(boolean field) {}
        record Y(@Inline X field) {}
        assertThrows(IllegalArgumentException.class, () -> gson.toJson(new Y(new X(true))));
    }

    @Test
    void duplicatePropertyRead() {
        record X(int field) {}
        assertThrows(JsonParseException.class, () -> parseFromResource("record/duplicate.json", X.class));
    }

    @Test
    @Disabled("we currently allow missing properties until a better solution is found")
    void missingProperty() {
        record X(String a, String b) {}
        assertThrows(JsonParseException.class, () -> parseFromResource("record/missing.json", X.class));
    }

    @Test
    @Disabled("see above")
    void nullNotNullable() {
        record X(String a) {}
        assertThrows(JsonParseException.class, () -> parseFromResource("record/null-not-nullable.json", X.class));
    }

    @Test
    void inlineRecordMultipleFields() {
        @Inline
        record X(String a, String b) {}
        assertThrows(IllegalArgumentException.class, () -> gson.getAdapter(X.class));
    }

    record Foo(
            int primitivεä,
            @SerializedName("I am different") String javaName,
            boolean yesNo,
            @Default("{\"nested\":3.14}") Bar hasDefault,
            @Inline Baz inlined,
            @Nullable String imNullable) {}

    record Bar(double nested) {}

    record Baz(@Default("1") int num, Bar bar) {}
}
