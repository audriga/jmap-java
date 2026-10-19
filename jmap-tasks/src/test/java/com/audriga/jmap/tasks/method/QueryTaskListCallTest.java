package com.audriga.jmap.tasks.method;

import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.gson.GsonUtils;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// No server implements TaskList/query yet, so this only checks that the call and response
// classes serialize and deserialize correctly on their own.
public class QueryTaskListCallTest {

    @Test
    public void callRoundTrip() {
        QueryTaskListCall call = QueryTaskListCall.builder()
                .accountId("account1")
                .filter(TaskListFilterCondition.builder().isVisible(true).build())
                .limit(10L)
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(call);
        QueryTaskListCall parsed = GsonUtils.REGULAR_GSON.fromJson(json, QueryTaskListCall.class);

        Assertions.assertEquals("account1", MethodCall.Arg.unwrapValue(parsed.accountId()));
        Assertions.assertEquals(10L, MethodCall.Arg.unwrapValue(parsed.limit()));
    }

    @Test
    public void responseRoundTrip() {
        QueryTaskListResponse response = QueryTaskListResponse.builder()
                .accountId("account1")
                .queryState("1")
                .canCalculateChanges(true)
                .position(0L)
                .ids(List.of("tasklist1"))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(response);
        QueryTaskListResponse parsed = GsonUtils.REGULAR_GSON.fromJson(json, QueryTaskListResponse.class);

        Assertions.assertEquals("account1", parsed.accountId());
        Assertions.assertEquals(List.of("tasklist1"), parsed.ids());
    }
}
