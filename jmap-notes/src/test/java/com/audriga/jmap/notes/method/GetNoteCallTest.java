package com.audriga.jmap.notes.method;

import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.gson.GsonUtils;
import com.audriga.jmap.notes.entity.Note;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// No server implements Note/get yet, so this only checks that the call and response
// classes serialize and deserialize correctly on their own.
public class GetNoteCallTest {

    @Test
    public void callRoundTrip() {
        GetNoteCall call = GetNoteCall.builder()
                .accountId("account1")
                .ids(List.of("note1"))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(call);
        GetNoteCall parsed = GsonUtils.REGULAR_GSON.fromJson(json, GetNoteCall.class);

        Assertions.assertEquals("account1", MethodCall.Arg.unwrapValue(parsed.accountId()));
        Assertions.assertEquals(List.of("note1"), MethodCall.Arg.unwrapValue(parsed.ids()));
    }

    @Test
    public void responseRoundTrip() {
        Note note = Note.builder()
                .id("note1")
                .notebookId("notebook1")
                .created(Instant.now())
                .updated(Instant.now())
                .sequence(1L)
                .name("Shopping list")
                .body("Milk, eggs")
                .bodyContentType("text/plain")
                .keywords(Map.of("Foo", true))
                .categories(Map.of("Bar", true))
                .color("#e57373")
                .build();

        GetNoteResponse response = GetNoteResponse.builder()
                .accountId("account1")
                .state("1")
                .list(List.of(note))
                .notFound(List.of())
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(response);
        GetNoteResponse parsed = GsonUtils.REGULAR_GSON.fromJson(json, GetNoteResponse.class);

        Assertions.assertEquals("account1", parsed.accountId());
        Assertions.assertEquals(1, parsed.list().size());
        Assertions.assertEquals("Shopping list", parsed.list().get(0).name());
        Assertions.assertEquals("notebook1", parsed.list().get(0).notebookId());
    }
}
