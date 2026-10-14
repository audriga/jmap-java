package com.audriga.jmap.notes.method;

import com.audriga.jmap.calendars.entity.CalendarRights;
import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.gson.GsonUtils;
import com.audriga.jmap.notes.entity.Notebook;
import com.audriga.jmap.notes.entity.NotebookRole;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// No server implements Notebook/get yet, so this only checks that the call and response
// classes serialize and deserialize correctly on their own.
public class GetNotebookCallTest {

    @Test
    public void callRoundTrip() {
        GetNotebookCall call = GetNotebookCall.builder()
                .accountId("account1")
                .ids(List.of("notebook1"))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(call);
        GetNotebookCall parsed = GsonUtils.REGULAR_GSON.fromJson(json, GetNotebookCall.class);

        Assertions.assertEquals("account1", MethodCall.Arg.unwrapValue(parsed.accountId()));
        Assertions.assertEquals(List.of("notebook1"), MethodCall.Arg.unwrapValue(parsed.ids()));
    }

    @Test
    public void responseRoundTrip() {
        Notebook notebook = Notebook.builder()
                .id("notebook1")
                .name("Personal")
                .isSubscribed(true)
                .isVisible(true)
                .role(NotebookRole.INBOX)
                .description("My personal notes")
                .shareWith(Map.of(
                        "friend1",
                        CalendarRights.builder()
                                .mayReadItems(true)
                                .mayWriteAll(false)
                                .build()))
                .build();

        GetNotebookResponse response = GetNotebookResponse.builder()
                .accountId("account1")
                .state("1")
                .list(List.of(notebook))
                .notFound(List.of())
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(response);
        GetNotebookResponse parsed = GsonUtils.REGULAR_GSON.fromJson(json, GetNotebookResponse.class);

        Assertions.assertEquals("account1", parsed.accountId());
        Assertions.assertEquals(1, parsed.list().size());
        Assertions.assertEquals("Personal", parsed.list().get(0).name());
        Assertions.assertEquals(NotebookRole.INBOX, parsed.list().get(0).role());
        Assertions.assertTrue(parsed.list().get(0).shareWith().get("friend1").mayReadItems());
    }
}
