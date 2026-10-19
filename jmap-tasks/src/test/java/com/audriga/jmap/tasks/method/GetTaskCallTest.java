package com.audriga.jmap.tasks.method;

import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.gson.GsonUtils;
import com.audriga.jmap.tasks.entity.Task;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// No server implements Task/get yet, so this only checks that the call and response
// classes serialize and deserialize correctly on their own.
public class GetTaskCallTest {

    @Test
    public void callRoundTrip() {
        GetTaskCall call = GetTaskCall.builder()
                .accountId("account1")
                .ids(List.of("task1"))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(call);
        GetTaskCall parsed = GsonUtils.REGULAR_GSON.fromJson(json, GetTaskCall.class);

        Assertions.assertEquals("account1", MethodCall.Arg.unwrapValue(parsed.accountId()));
        Assertions.assertEquals(List.of("task1"), MethodCall.Arg.unwrapValue(parsed.ids()));
    }

    @Test
    public void responseRoundTrip() {
        Task task = Task.builder()
                .id("task1")
                .taskListId("tasklist1")
                .uid("uid1")
                .created(Instant.now())
                .updated(Instant.now())
                .sequence(1L)
                .title("Buy groceries")
                .description("Milk, eggs")
                .keywords(Set.of("Foo"))
                .categories(Set.of("Bar"))
                .percentComplete(50L)
                .progress("in-process")
                .build();

        GetTaskResponse response = GetTaskResponse.builder()
                .accountId("account1")
                .state("1")
                .list(List.of(task))
                .notFound(List.of())
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(response);
        GetTaskResponse parsed = GsonUtils.REGULAR_GSON.fromJson(json, GetTaskResponse.class);

        Assertions.assertEquals("account1", parsed.accountId());
        Assertions.assertEquals(1, parsed.list().size());
        Assertions.assertEquals("Buy groceries", parsed.list().get(0).title());
        Assertions.assertEquals("tasklist1", parsed.list().get(0).taskListId());
    }
}
