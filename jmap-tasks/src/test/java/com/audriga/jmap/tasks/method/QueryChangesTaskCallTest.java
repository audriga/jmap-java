package com.audriga.jmap.tasks.method;

import com.audriga.jmap.common.entity.AddedItem;
import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.gson.GsonUtils;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// No server implements Task/queryChanges yet, so this only checks that the call and response
// classes serialize and deserialize correctly on their own.
public class QueryChangesTaskCallTest {

    @Test
    public void callRoundTrip() {
        QueryChangesTaskCall call = QueryChangesTaskCall.builder()
                .accountId("account1")
                .sinceQueryState("1")
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(call);
        QueryChangesTaskCall parsed = GsonUtils.REGULAR_GSON.fromJson(json, QueryChangesTaskCall.class);

        Assertions.assertEquals("account1", MethodCall.Arg.unwrapValue(parsed.accountId()));
        Assertions.assertEquals("1", MethodCall.Arg.unwrapValue(parsed.sinceQueryState()));
    }

    @Test
    public void responseRoundTrip() {
        QueryChangesTaskResponse response = QueryChangesTaskResponse.builder()
                .accountId("account1")
                .oldQueryState("1")
                .newQueryState("2")
                .total(1)
                .removed(List.of())
                .added(List.of(AddedItem.of("task1", 0L)))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(response);
        QueryChangesTaskResponse parsed = GsonUtils.REGULAR_GSON.fromJson(json, QueryChangesTaskResponse.class);

        Assertions.assertEquals("account1", parsed.accountId());
        Assertions.assertEquals("task1", parsed.added().get(0).getItem());
    }
}
