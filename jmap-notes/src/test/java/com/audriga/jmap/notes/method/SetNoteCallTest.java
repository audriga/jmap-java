package com.audriga.jmap.notes.method;

import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.gson.GsonUtils;
import com.audriga.jmap.notes.entity.Note;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// Only checks that the call and response classes serialize and deserialize correctly
// on their own; see NoteLiveMockIT and NoteMockServerIT for integration coverage of Note/set.
public class SetNoteCallTest {

    @Test
    public void callRoundTrip() {
        SetNoteCall call = SetNoteCall.builder()
                .accountId("account1")
                .create(Map.of(
                        "a", Note.builder().name("Shopping list").body("Milk").build()))
                .destroy(List.of("note2"))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(call);
        SetNoteCall parsed = GsonUtils.REGULAR_GSON.fromJson(json, SetNoteCall.class);

        Assertions.assertEquals("account1", MethodCall.Arg.unwrapValue(parsed.accountId()));
        Assertions.assertEquals(
                "Shopping list",
                MethodCall.Arg.unwrapValue(parsed.create()).get("a").name());
        Assertions.assertEquals(List.of("note2"), MethodCall.Arg.unwrapValue(parsed.destroy()));
    }

    @Test
    public void responseRoundTrip() {
        SetNoteResponse response = SetNoteResponse.builder()
                .accountId("account1")
                .newState("2")
                .created(Map.of(
                        "a", Note.builder().id("note1").name("Shopping list").build()))
                .destroyed(List.of("note2"))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(response);
        SetNoteResponse parsed = GsonUtils.REGULAR_GSON.fromJson(json, SetNoteResponse.class);

        Assertions.assertEquals("account1", parsed.accountId());
        Assertions.assertEquals("note1", parsed.created().get("a").id());
        Assertions.assertEquals(List.of("note2"), parsed.destroyed());
    }
}
