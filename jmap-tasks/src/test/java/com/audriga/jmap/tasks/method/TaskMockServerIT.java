package com.audriga.jmap.tasks.method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.audriga.jmap.client.ConnectionConfig;
import com.audriga.jmap.client.JmapClient;
import com.audriga.jmap.client.http.BasicAuthHttpAuthentication;
import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.tasks.entity.Task;
import com.audriga.jmap.tasks.entity.TaskList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import okhttp3.HttpUrl;
import org.junit.jupiter.api.Test;

// Runs against a locally started jmap-php-mock instance (php -S localhost:8765 jmap.php),
// not started automatically by this test.
class TaskMockServerIT {

    private static final HttpUrl BASE_URL = new HttpUrl.Builder()
            .scheme("http")
            .host("localhost")
            .port(8765)
            .encodedPath("/.well-known/jmap")
            .build();

    @Test
    void getTasks() throws ExecutionException, InterruptedException {
        try (var client = new JmapClient(
                new ConnectionConfig(new BasicAuthHttpAuthentication("testuser", "secret"), BASE_URL, null))) {
            var res = client.call(new GetTaskCall(MethodCall.Arg.of("mockAccount"), null, null))
                    .get()
                    .getMain(GetTaskResponse.class);
            assertNotNull(res.list());
        }
    }

    @Test
    void setTask() throws ExecutionException, InterruptedException {
        try (var client = new JmapClient(
                new ConnectionConfig(new BasicAuthHttpAuthentication("testuser", "secret"), BASE_URL, null))) {
            var res = client.call(new SetTaskCall(
                            MethodCall.Arg.of("mockAccount"),
                            null,
                            MethodCall.Arg.of(Map.of(
                                    "a",
                                    Task.builder()
                                            .title("Buy groceries")
                                            .description("Milk, eggs")
                                            .taskListId("123")
                                            .build())),
                            null,
                            null))
                    .get()
                    .getMain(SetTaskResponse.class);
            assertEquals(0, res.notCreated() == null ? 0 : res.notCreated().size());
            String createdId = res.created().get("a").id();
            assertNotNull(createdId);

            var destroyRes = client.call(new SetTaskCall(
                            MethodCall.Arg.of("mockAccount"), null, null, null, MethodCall.Arg.of(List.of(createdId))))
                    .get()
                    .getMain(SetTaskResponse.class);
            assertEquals(
                    0,
                    destroyRes.notDestroyed() == null
                            ? 0
                            : destroyRes.notDestroyed().size());
            assertEquals(List.of(createdId), destroyRes.destroyed());
        }
    }

    @Test
    void getTaskLists() throws ExecutionException, InterruptedException {
        try (var client = new JmapClient(
                new ConnectionConfig(new BasicAuthHttpAuthentication("testuser", "secret"), BASE_URL, null))) {
            var res = client.call(new GetTaskListCall(MethodCall.Arg.of("mockAccount"), null, null))
                    .get()
                    .getMain(GetTaskListResponse.class);
            assertNotNull(res.list());
        }
    }

    @Test
    void setTaskList() throws ExecutionException, InterruptedException {
        try (var client = new JmapClient(
                new ConnectionConfig(new BasicAuthHttpAuthentication("testuser", "secret"), BASE_URL, null))) {
            var res = client.call(new SetTaskListCall(
                            MethodCall.Arg.of("mockAccount"),
                            null,
                            MethodCall.Arg.of(Map.of(
                                    "a", TaskList.builder().name("Personal").build())),
                            null,
                            null))
                    .get()
                    .getMain(SetTaskListResponse.class);
            assertEquals(0, res.notCreated() == null ? 0 : res.notCreated().size());
            String createdId = res.created().get("a").id();
            assertNotNull(createdId);

            var destroyRes = client.call(new SetTaskListCall(
                            MethodCall.Arg.of("mockAccount"), null, null, null, MethodCall.Arg.of(List.of(createdId))))
                    .get()
                    .getMain(SetTaskListResponse.class);
            assertEquals(
                    0,
                    destroyRes.notDestroyed() == null
                            ? 0
                            : destroyRes.notDestroyed().size());
            assertEquals(List.of(createdId), destroyRes.destroyed());
        }
    }
}
