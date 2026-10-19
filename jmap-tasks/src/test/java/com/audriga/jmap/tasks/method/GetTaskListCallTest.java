package com.audriga.jmap.tasks.method;

import com.audriga.jmap.calendars.entity.CalendarRights;
import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.gson.GsonUtils;
import com.audriga.jmap.tasks.entity.TaskList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// No server implements TaskList/get yet, so this only checks that the call and response
// classes serialize and deserialize correctly on their own.
public class GetTaskListCallTest {

    @Test
    public void callRoundTrip() {
        GetTaskListCall call = GetTaskListCall.builder()
                .accountId("account1")
                .ids(List.of("tasklist1"))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(call);
        GetTaskListCall parsed = GsonUtils.REGULAR_GSON.fromJson(json, GetTaskListCall.class);

        Assertions.assertEquals("account1", MethodCall.Arg.unwrapValue(parsed.accountId()));
        Assertions.assertEquals(List.of("tasklist1"), MethodCall.Arg.unwrapValue(parsed.ids()));
    }

    @Test
    public void responseRoundTrip() {
        TaskList taskList = TaskList.builder()
                .id("tasklist1")
                .name("Personal")
                .isSubscribed(true)
                .isVisible(true)
                .description("My personal tasks")
                .shareWith(Map.of(
                        "friend1",
                        CalendarRights.builder()
                                .mayReadItems(true)
                                .mayWriteAll(false)
                                .build()))
                .build();

        GetTaskListResponse response = GetTaskListResponse.builder()
                .accountId("account1")
                .state("1")
                .list(List.of(taskList))
                .notFound(List.of())
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(response);
        GetTaskListResponse parsed = GsonUtils.REGULAR_GSON.fromJson(json, GetTaskListResponse.class);

        Assertions.assertEquals("account1", parsed.accountId());
        Assertions.assertEquals(1, parsed.list().size());
        Assertions.assertEquals("Personal", parsed.list().get(0).name());
        Assertions.assertTrue(parsed.list().get(0).shareWith().get("friend1").mayReadItems());
    }
}
