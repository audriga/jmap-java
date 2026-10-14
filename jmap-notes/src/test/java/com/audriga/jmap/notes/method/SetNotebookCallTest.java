package com.audriga.jmap.notes.method;

import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.gson.GsonUtils;
import com.audriga.jmap.notes.entity.Notebook;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// No server implements Notebook/set yet, so this only checks that the call and response
// classes serialize and deserialize correctly on their own.
public class SetNotebookCallTest {

    @Test
    public void callRoundTrip() {
        SetNotebookCall call = SetNotebookCall.builder()
                .accountId("account1")
                .create(Map.of("a", Notebook.builder().name("Personal").build()))
                .destroy(List.of("notebook2"))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(call);
        SetNotebookCall parsed = GsonUtils.REGULAR_GSON.fromJson(json, SetNotebookCall.class);

        Assertions.assertEquals("account1", MethodCall.Arg.unwrapValue(parsed.accountId()));
        Assertions.assertEquals(
                "Personal", MethodCall.Arg.unwrapValue(parsed.create()).get("a").name());
        Assertions.assertEquals(List.of("notebook2"), MethodCall.Arg.unwrapValue(parsed.destroy()));
    }

    @Test
    public void responseRoundTrip() {
        SetNotebookResponse response = SetNotebookResponse.builder()
                .accountId("account1")
                .newState("2")
                .created(Map.of(
                        "a", Notebook.builder().id("notebook1").name("Personal").build()))
                .destroyed(List.of("notebook2"))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(response);
        SetNotebookResponse parsed = GsonUtils.REGULAR_GSON.fromJson(json, SetNotebookResponse.class);

        Assertions.assertEquals("account1", parsed.accountId());
        Assertions.assertEquals("notebook1", parsed.created().get("a").id());
        Assertions.assertEquals(List.of("notebook2"), parsed.destroyed());
    }
}
