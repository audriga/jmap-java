package com.audriga.jmap.tasks.method;

import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.gson.GsonUtils;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// No server implements TaskList/changes yet, so this only checks that the call and response
// classes serialize and deserialize correctly on their own.
public class ChangesTaskListCallTest {

    @Test
    public void callRoundTrip() {
        ChangesTaskListCall call = ChangesTaskListCall.builder()
                .accountId("account1")
                .sinceState("1")
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(call);
        ChangesTaskListCall parsed = GsonUtils.REGULAR_GSON.fromJson(json, ChangesTaskListCall.class);

        Assertions.assertEquals("account1", MethodCall.Arg.unwrapValue(parsed.accountId()));
        Assertions.assertEquals("1", MethodCall.Arg.unwrapValue(parsed.sinceState()));
    }

    @Test
    public void responseRoundTrip() {
        ChangesTaskListResponse response = ChangesTaskListResponse.builder()
                .accountId("account1")
                .oldState("1")
                .newState("2")
                .hasMoreChanges(false)
                .created(List.of("tasklist1"))
                .updated(List.of())
                .destroyed(List.of())
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(response);
        ChangesTaskListResponse parsed = GsonUtils.REGULAR_GSON.fromJson(json, ChangesTaskListResponse.class);

        Assertions.assertEquals("account1", parsed.accountId());
        Assertions.assertEquals(List.of("tasklist1"), parsed.created());
    }
}
