package com.audriga.jmap.tasks.method;

import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.gson.GsonUtils;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// No server implements Task/query yet, so this only checks that the call and response
// classes serialize and deserialize correctly on their own.
public class QueryTaskCallTest {

    @Test
    public void callRoundTrip() {
        QueryTaskCall call = QueryTaskCall.builder()
                .accountId("account1")
                .filter(TaskFilterCondition.builder().taskListId("tasklist1").build())
                .limit(10L)
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(call);
        QueryTaskCall parsed = GsonUtils.REGULAR_GSON.fromJson(json, QueryTaskCall.class);

        Assertions.assertEquals("account1", MethodCall.Arg.unwrapValue(parsed.accountId()));
        Assertions.assertEquals(10L, MethodCall.Arg.unwrapValue(parsed.limit()));
    }

    @Test
    public void responseRoundTrip() {
        QueryTaskResponse response = QueryTaskResponse.builder()
                .accountId("account1")
                .queryState("1")
                .canCalculateChanges(true)
                .position(0L)
                .ids(List.of("task1"))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(response);
        QueryTaskResponse parsed = GsonUtils.REGULAR_GSON.fromJson(json, QueryTaskResponse.class);

        Assertions.assertEquals("account1", parsed.accountId());
        Assertions.assertEquals(List.of("task1"), parsed.ids());
    }
}
