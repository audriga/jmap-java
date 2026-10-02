package com.audriga.jmap.filenode;

import static org.junit.jupiter.api.Assertions.*;

import com.audriga.jmap.client.ConnectionConfig;
import com.audriga.jmap.client.JmapClient;
import com.audriga.jmap.client.http.BasicAuthHttpAuthentication;
import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.filenode.entity.FileNode;
import com.audriga.jmap.filenode.method.*;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

class FileNodeIT {

    @Test
    void run() throws ExecutionException, InterruptedException {
        try (var server = StalwartContainer.latest()) {
            server.start();
            try (var client = new JmapClient(new ConnectionConfig(
                    new BasicAuthHttpAuthentication(server.username(), server.password()),
                    server.publicUrl().resolve("/.well-known/jmap"),
                    InsecureX509TrustManager.INSTANCE))) {
                var session = client.getSession().get();
                var accountId = session.getPrimaryAccount(FileNodeAccountCapability.class);

                String createdId;
                {
                    var res = client.call(new SetFileNodeCall(
                                    MethodCall.Arg.of(accountId),
                                    null,
                                    MethodCall.Arg.of(Map.of(
                                            "a",
                                            FileNode.builder()
                                                    .nodeType("directory")
                                                    .name("test-folder")
                                                    .created(Instant.now())
                                                    .build())),
                                    null,
                                    null))
                            .get()
                            .getMain(SetFileNodeResponse.class);
                    assertNull(res.notCreated());
                    assertEquals(1, res.created().size());
                    createdId = res.created().get("a").id();
                    assertNotNull(createdId);
                }
                {
                    var res = client.call(new GetFileNodeCall(MethodCall.Arg.of(accountId), null, null))
                            .get()
                            .getMain(GetFileNodeResponse.class);
                    assertTrue(res.list().stream().anyMatch(node -> node.id().equals(createdId)));
                }
                String queryState;
                {
                    var res = client.call(new QueryFileNodeCall(
                                    MethodCall.Arg.of(accountId),
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    MethodCall.Arg.of(true)))
                            .get()
                            .getMain(QueryFileNodeResponse.class);
                    assertTrue(res.ids().contains(createdId));
                    queryState = res.queryState();
                }
                String secondId;
                {
                    var res = client.call(new SetFileNodeCall(
                                    MethodCall.Arg.of(accountId),
                                    null,
                                    MethodCall.Arg.of(Map.of(
                                            "b",
                                            FileNode.builder()
                                                    .nodeType("directory")
                                                    .name("test-folder-2")
                                                    .created(Instant.now())
                                                    .build())),
                                    null,
                                    null))
                            .get()
                            .getMain(SetFileNodeResponse.class);
                    assertNull(res.notCreated());
                    secondId = res.created().get("b").id();
                    assertNotNull(secondId);
                }
                {
                    var res = client.call(new QueryChangesFileNodeCall(
                                    MethodCall.Arg.of(accountId),
                                    null,
                                    null,
                                    MethodCall.Arg.of(queryState),
                                    null,
                                    null,
                                    null))
                            .get()
                            .getMain(QueryChangesFileNodeResponse.class);
                    assertTrue(
                            res.added().stream().anyMatch(item -> item.getItem().equals(secondId)));
                }
                {
                    var res = client.call(new SetFileNodeCall(
                                    MethodCall.Arg.of(accountId),
                                    null,
                                    null,
                                    null,
                                    MethodCall.Arg.of(java.util.List.of(createdId, secondId))))
                            .get()
                            .getMain(SetFileNodeResponse.class);
                    assertNull(res.notDestroyed());
                    assertTrue(res.destroyed().contains(createdId));
                    assertTrue(res.destroyed().contains(secondId));
                }
            }
        }
    }
}
