package com.audriga.jmap.tasks.method;

import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.gson.GsonUtils;
import com.audriga.jmap.tasks.entity.TaskList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// Only checks that the call and response classes serialize and deserialize correctly
// on their own; see TaskLiveMockIT and TaskMockServerIT for integration coverage of TaskList/set.
public class SetTaskListCallTest {

    @Test
    public void callRoundTrip() {
        SetTaskListCall call = SetTaskListCall.builder()
                .accountId("account1")
                .create(Map.of("a", TaskList.builder().name("Personal").build()))
                .destroy(List.of("tasklist2"))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(call);
        SetTaskListCall parsed = GsonUtils.REGULAR_GSON.fromJson(json, SetTaskListCall.class);

        Assertions.assertEquals("account1", MethodCall.Arg.unwrapValue(parsed.accountId()));
        Assertions.assertEquals(
                "Personal", MethodCall.Arg.unwrapValue(parsed.create()).get("a").name());
        Assertions.assertEquals(List.of("tasklist2"), MethodCall.Arg.unwrapValue(parsed.destroy()));
    }

    @Test
    public void responseRoundTrip() {
        SetTaskListResponse response = SetTaskListResponse.builder()
                .accountId("account1")
                .newState("2")
                .created(Map.of(
                        "a", TaskList.builder().id("tasklist1").name("Personal").build()))
                .destroyed(List.of("tasklist2"))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(response);
        SetTaskListResponse parsed = GsonUtils.REGULAR_GSON.fromJson(json, SetTaskListResponse.class);

        Assertions.assertEquals("account1", parsed.accountId());
        Assertions.assertEquals("tasklist1", parsed.created().get("a").id());
        Assertions.assertEquals(List.of("tasklist2"), parsed.destroyed());
    }
}
