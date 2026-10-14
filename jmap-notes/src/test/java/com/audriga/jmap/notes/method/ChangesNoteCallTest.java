package com.audriga.jmap.notes.method;

import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.gson.GsonUtils;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// No server implements Note/changes yet, so this only checks that the call and response
// classes serialize and deserialize correctly on their own.
public class ChangesNoteCallTest {

    @Test
    public void callRoundTrip() {
        ChangesNoteCall call =
                ChangesNoteCall.builder().accountId("account1").sinceState("1").build();

        String json = GsonUtils.REGULAR_GSON.toJson(call);
        ChangesNoteCall parsed = GsonUtils.REGULAR_GSON.fromJson(json, ChangesNoteCall.class);

        Assertions.assertEquals("account1", MethodCall.Arg.unwrapValue(parsed.accountId()));
        Assertions.assertEquals("1", MethodCall.Arg.unwrapValue(parsed.sinceState()));
    }

    @Test
    public void responseRoundTrip() {
        ChangesNoteResponse response = ChangesNoteResponse.builder()
                .accountId("account1")
                .oldState("1")
                .newState("2")
                .hasMoreChanges(false)
                .created(List.of("note1"))
                .updated(List.of())
                .destroyed(List.of())
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(response);
        ChangesNoteResponse parsed = GsonUtils.REGULAR_GSON.fromJson(json, ChangesNoteResponse.class);

        Assertions.assertEquals("account1", parsed.accountId());
        Assertions.assertEquals(List.of("note1"), parsed.created());
    }
}
