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
                }
                {
                    var res = client.call(new SetFileNodeCall(
                                    MethodCall.Arg.of(accountId),
                                    null,
                                    null,
                                    null,
                                    MethodCall.Arg.of(java.util.List.of(createdId))))
                            .get()
                            .getMain(SetFileNodeResponse.class);
                    assertNull(res.notDestroyed());
                    assertTrue(res.destroyed().contains(createdId));
                }
            }
        }
    }
}
