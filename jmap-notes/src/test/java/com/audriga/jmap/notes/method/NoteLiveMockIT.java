package com.audriga.jmap.notes.method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.audriga.jmap.client.ConnectionConfig;
import com.audriga.jmap.client.JmapClient;
import com.audriga.jmap.client.http.BasicAuthHttpAuthentication;
import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.notes.entity.Note;
import com.audriga.jmap.notes.entity.Notebook;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import okhttp3.HttpUrl;
import org.junit.jupiter.api.Test;

// Runs against the maintained OpenXPort mock deployment at audriga.eu. Writes are disabled
// there by default, so the session URL carries a query parameter to opt in for this test.
// That parameter gets echoed back into the session's apiUrl and carries over to every call.
class NoteLiveMockIT {

    private static final HttpUrl BASE_URL = new HttpUrl.Builder()
            .scheme("https")
            .host("www.audriga.eu")
            .encodedPath("/test/openxport/mock/jmap.php")
            .addQueryParameter("Mock-Jmap-Set-Enabled", "true")
            .build();

    @Test
    void getNotes() throws ExecutionException, InterruptedException {
        try (var client = new JmapClient(
                new ConnectionConfig(new BasicAuthHttpAuthentication("testuser", "secret"), BASE_URL, null))) {
            var res = client.call(new GetNoteCall(MethodCall.Arg.of("mockAccount"), null, null))
                    .get()
                    .getMain(GetNoteResponse.class);
            assertNotNull(res.list());
        }
    }

    @Test
    void setNote() throws ExecutionException, InterruptedException {
        try (var client = new JmapClient(
                new ConnectionConfig(new BasicAuthHttpAuthentication("testuser", "secret"), BASE_URL, null))) {
            var res = client.call(new SetNoteCall(
                            MethodCall.Arg.of("mockAccount"),
                            null,
                            MethodCall.Arg.of(Map.of(
                                    "a",
                                    Note.builder()
                                            .name("shopping list")
                                            .body("milk, eggs")
                                            .notebookId("123")
                                            .build())),
                            null,
                            null))
                    .get()
                    .getMain(SetNoteResponse.class);
            assertEquals(0, res.notCreated() == null ? 0 : res.notCreated().size());
            String createdId = res.created().get("a").id();
            assertNotNull(createdId);

            var destroyRes = client.call(new SetNoteCall(
                            MethodCall.Arg.of("mockAccount"), null, null, null, MethodCall.Arg.of(List.of(createdId))))
                    .get()
                    .getMain(SetNoteResponse.class);
            assertEquals(
                    0,
                    destroyRes.notDestroyed() == null
                            ? 0
                            : destroyRes.notDestroyed().size());
            assertEquals(List.of(createdId), destroyRes.destroyed());
        }
    }

    @Test
    void getNotebooks() throws ExecutionException, InterruptedException {
        try (var client = new JmapClient(
                new ConnectionConfig(new BasicAuthHttpAuthentication("testuser", "secret"), BASE_URL, null))) {
            var res = client.call(new GetNotebookCall(MethodCall.Arg.of("mockAccount"), null, null))
                    .get()
                    .getMain(GetNotebookResponse.class);
            assertNotNull(res.list());
        }
    }

    @Test
    void setNotebook() throws ExecutionException, InterruptedException {
        try (var client = new JmapClient(
                new ConnectionConfig(new BasicAuthHttpAuthentication("testuser", "secret"), BASE_URL, null))) {
            var res = client.call(new SetNotebookCall(
                            MethodCall.Arg.of("mockAccount"),
                            null,
                            MethodCall.Arg.of(Map.of(
                                    "a", Notebook.builder().name("Personal").build())),
                            null,
                            null))
                    .get()
                    .getMain(SetNotebookResponse.class);
            assertEquals(0, res.notCreated() == null ? 0 : res.notCreated().size());
            String createdId = res.created().get("a").id();
            assertNotNull(createdId);

            var destroyRes = client.call(new SetNotebookCall(
                            MethodCall.Arg.of("mockAccount"), null, null, null, MethodCall.Arg.of(List.of(createdId))))
                    .get()
                    .getMain(SetNotebookResponse.class);
            assertEquals(
                    0,
                    destroyRes.notDestroyed() == null
                            ? 0
                            : destroyRes.notDestroyed().size());
            assertEquals(List.of(createdId), destroyRes.destroyed());
        }
    }
}
