package com.audriga.jmap.notes.method;

import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.gson.GsonUtils;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// No server implements Note/query yet, so this only checks that the call and response
// classes serialize and deserialize correctly on their own.
public class QueryNoteCallTest {

    @Test
    public void callRoundTrip() {
        QueryNoteCall call = QueryNoteCall.builder()
                .accountId("account1")
                .filter(NoteFilterCondition.builder().notebookId("notebook1").build())
                .limit(10L)
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(call);
        QueryNoteCall parsed = GsonUtils.REGULAR_GSON.fromJson(json, QueryNoteCall.class);

        Assertions.assertEquals("account1", MethodCall.Arg.unwrapValue(parsed.accountId()));
        Assertions.assertEquals(10L, MethodCall.Arg.unwrapValue(parsed.limit()));
    }

    @Test
    public void responseRoundTrip() {
        QueryNoteResponse response = QueryNoteResponse.builder()
                .accountId("account1")
                .queryState("1")
                .canCalculateChanges(true)
                .position(0L)
                .ids(List.of("note1"))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(response);
        QueryNoteResponse parsed = GsonUtils.REGULAR_GSON.fromJson(json, QueryNoteResponse.class);

        Assertions.assertEquals("account1", parsed.accountId());
        Assertions.assertEquals(List.of("note1"), parsed.ids());
    }
}
