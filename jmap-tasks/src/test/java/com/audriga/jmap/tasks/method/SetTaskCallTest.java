package com.audriga.jmap.tasks.method;

import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.gson.GsonUtils;
import com.audriga.jmap.tasks.entity.Task;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// No server implements Task/set yet, so this only checks that the call and response
// classes serialize and deserialize correctly on their own.
public class SetTaskCallTest {

    @Test
    public void callRoundTrip() {
        SetTaskCall call = SetTaskCall.builder()
                .accountId("account1")
                .create(Map.of(
                        "a",
                        Task.builder()
                                .title("Buy groceries")
                                .taskListId("tasklist1")
                                .build()))
                .destroy(List.of("task2"))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(call);
        SetTaskCall parsed = GsonUtils.REGULAR_GSON.fromJson(json, SetTaskCall.class);

        Assertions.assertEquals("account1", MethodCall.Arg.unwrapValue(parsed.accountId()));
        Assertions.assertEquals(
                "Buy groceries",
                MethodCall.Arg.unwrapValue(parsed.create()).get("a").title());
        Assertions.assertEquals(List.of("task2"), MethodCall.Arg.unwrapValue(parsed.destroy()));
    }

    @Test
    public void responseRoundTrip() {
        SetTaskResponse response = SetTaskResponse.builder()
                .accountId("account1")
                .newState("2")
                .created(Map.of(
                        "a", Task.builder().id("task1").title("Buy groceries").build()))
                .destroyed(List.of("task2"))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(response);
        SetTaskResponse parsed = GsonUtils.REGULAR_GSON.fromJson(json, SetTaskResponse.class);

        Assertions.assertEquals("account1", parsed.accountId());
        Assertions.assertEquals("task1", parsed.created().get("a").id());
        Assertions.assertEquals(List.of("task2"), parsed.destroyed());
    }
}
