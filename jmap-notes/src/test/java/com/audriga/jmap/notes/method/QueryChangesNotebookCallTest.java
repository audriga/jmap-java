package com.audriga.jmap.notes.method;

import com.audriga.jmap.common.entity.AddedItem;
import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.gson.GsonUtils;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// No server implements Notebook/queryChanges yet, so this only checks that the call and
// response classes serialize and deserialize correctly on their own.
public class QueryChangesNotebookCallTest {

    @Test
    public void callRoundTrip() {
        QueryChangesNotebookCall call = QueryChangesNotebookCall.builder()
                .accountId("account1")
                .sinceQueryState("1")
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(call);
        QueryChangesNotebookCall parsed = GsonUtils.REGULAR_GSON.fromJson(json, QueryChangesNotebookCall.class);

        Assertions.assertEquals("account1", MethodCall.Arg.unwrapValue(parsed.accountId()));
        Assertions.assertEquals("1", MethodCall.Arg.unwrapValue(parsed.sinceQueryState()));
    }

    @Test
    public void responseRoundTrip() {
        QueryChangesNotebookResponse response = QueryChangesNotebookResponse.builder()
                .accountId("account1")
                .oldQueryState("1")
                .newQueryState("2")
                .total(1)
                .removed(List.of())
                .added(List.of(AddedItem.of("notebook1", 0L)))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(response);
        QueryChangesNotebookResponse parsed = GsonUtils.REGULAR_GSON.fromJson(json, QueryChangesNotebookResponse.class);

        Assertions.assertEquals("account1", parsed.accountId());
        Assertions.assertEquals("notebook1", parsed.added().get(0).getItem());
    }
}
