package com.audriga.jmap.notes.method;

import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.gson.GsonUtils;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// No server implements Notebook/query yet, so this only checks that the call and response
// classes serialize and deserialize correctly on their own.
public class QueryNotebookCallTest {

    @Test
    public void callRoundTrip() {
        QueryNotebookCall call = QueryNotebookCall.builder()
                .accountId("account1")
                .filter(NotebookFilterCondition.builder().isVisible(true).build())
                .limit(10L)
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(call);
        QueryNotebookCall parsed = GsonUtils.REGULAR_GSON.fromJson(json, QueryNotebookCall.class);

        Assertions.assertEquals("account1", MethodCall.Arg.unwrapValue(parsed.accountId()));
        Assertions.assertEquals(10L, MethodCall.Arg.unwrapValue(parsed.limit()));
    }

    @Test
    public void responseRoundTrip() {
        QueryNotebookResponse response = QueryNotebookResponse.builder()
                .accountId("account1")
                .queryState("1")
                .canCalculateChanges(true)
                .position(0L)
                .ids(List.of("notebook1"))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(response);
        QueryNotebookResponse parsed = GsonUtils.REGULAR_GSON.fromJson(json, QueryNotebookResponse.class);

        Assertions.assertEquals("account1", parsed.accountId());
        Assertions.assertEquals(List.of("notebook1"), parsed.ids());
    }
}
