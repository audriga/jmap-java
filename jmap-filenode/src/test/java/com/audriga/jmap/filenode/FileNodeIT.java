package com.audriga.jmap.filenode;

import static org.junit.jupiter.api.Assertions.*;

import com.audriga.jmap.client.ConnectionConfig;
import com.audriga.jmap.client.JmapClient;
import com.audriga.jmap.client.blob.Uploadable;
import com.audriga.jmap.client.http.BasicAuthHttpAuthentication;
import com.audriga.jmap.common.entity.Downloadable;
import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.filenode.entity.FileNode;
import com.audriga.jmap.filenode.method.*;
import com.audriga.jmap.stalwart.StalwartAccount;
import com.audriga.jmap.stalwart.StalwartCredential;
import com.audriga.jmap.stalwart.StalwartDomain;
import com.audriga.jmap.stalwart.StalwartEncryptionAtRest;
import com.audriga.jmap.stalwart.StalwartPasswordCredential;
import com.audriga.jmap.stalwart.StalwartPermissions;
import com.audriga.jmap.stalwart.StalwartUserAccount;
import com.audriga.jmap.stalwart.StalwartUserRoles;
import com.google.common.net.MediaType;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
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
                String stateAfterCreate;
                {
                    var res = client.call(new GetFileNodeCall(MethodCall.Arg.of(accountId), null, null))
                            .get()
                            .getMain(GetFileNodeResponse.class);
                    assertTrue(res.list().stream().anyMatch(node -> node.id().equals(createdId)));
                    stateAfterCreate = res.state();
                }
                {
                    var res = client.call(new SetFileNodeCall(
                                    MethodCall.Arg.of(accountId),
                                    null,
                                    null,
                                    MethodCall.Arg.of(Map.of(createdId, Map.of("name", "renamed-folder"))),
                                    null))
                            .get()
                            .getMain(SetFileNodeResponse.class);
                    assertNull(res.notUpdated());
                    assertTrue(res.updated().containsKey(createdId));
                }
                {
                    var res = client.call(new GetFileNodeCall(
                                    MethodCall.Arg.of(accountId), MethodCall.Arg.of(List.of(createdId)), null))
                            .get()
                            .getMain(GetFileNodeResponse.class);
                    assertEquals(1, res.list().size());
                    assertEquals("renamed-folder", res.list().get(0).name());
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
                    var res = client.call(new ChangesFileNodeCall(
                                    MethodCall.Arg.of(accountId), MethodCall.Arg.of(stateAfterCreate), null))
                            .get()
                            .getMain(ChangesFileNodeResponse.class);
                    assertTrue(res.created().contains(secondId));
                    assertFalse(res.created().contains(createdId));
                }
                String blobId;
                final byte[] content = "hello file node".getBytes(StandardCharsets.UTF_8);
                {
                    final var upload = client.upload(
                                    accountId,
                                    new Uploadable() {
                                        @Override
                                        public InputStream getInputStream() {
                                            return new ByteArrayInputStream(content);
                                        }

                                        @Override
                                        public MediaType getMediaType() {
                                            return MediaType.PLAIN_TEXT_UTF_8;
                                        }

                                        @Override
                                        public long getContentLength() {
                                            return content.length;
                                        }
                                    },
                                    progress -> {})
                            .get();
                    blobId = upload.blobId();
                    assertNotNull(blobId);
                }
                String fileId;
                {
                    var res = client.call(new SetFileNodeCall(
                                    MethodCall.Arg.of(accountId),
                                    null,
                                    MethodCall.Arg.of(Map.of(
                                            "f",
                                            FileNode.builder()
                                                    .nodeType("file")
                                                    .name("hello.txt")
                                                    .blobId(blobId)
                                                    .created(Instant.now())
                                                    .build())),
                                    null,
                                    null))
                            .get()
                            .getMain(SetFileNodeResponse.class);
                    assertNull(res.notCreated());
                    fileId = res.created().get("f").id();
                    assertNotNull(fileId);
                }
                {
                    final var download = client.download(accountId, new Downloadable() {
                                @Override
                                public String name() {
                                    return "hello.txt";
                                }

                                @Override
                                public String blobId() {
                                    return blobId;
                                }

                                @Override
                                public String type() {
                                    return "text/plain";
                                }

                                @Override
                                public Long size() {
                                    return (long) content.length;
                                }
                            })
                            .get();
                    final byte[] downloaded;
                    try (InputStream in = download.getInputStream()) {
                        downloaded = in.readAllBytes();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    assertEquals(
                            new String(content, StandardCharsets.UTF_8),
                            new String(downloaded, StandardCharsets.UTF_8));
                }
                {
                    var res = client.call(new SetFileNodeCall(
                                    MethodCall.Arg.of(accountId),
                                    null,
                                    null,
                                    null,
                                    MethodCall.Arg.of(List.of(createdId, secondId, fileId))))
                            .get()
                            .getMain(SetFileNodeResponse.class);
                    assertNull(res.notDestroyed());
                    assertTrue(res.destroyed().contains(createdId));
                    assertTrue(res.destroyed().contains(secondId));
                    assertTrue(res.destroyed().contains(fileId));
                }
            }
        }
    }

    // FileNode/copy needs two separate accounts, stalwart rejects copy within the same
    // account. The second account is created via the admin-only x:Account/set extension,
    // then logged into separately to get its own JMAP accountId.
    @Test
    void copy() throws ExecutionException, InterruptedException {
        try (var server = StalwartContainer.latest()) {
            server.start();
            try (var adminClient = new JmapClient(new ConnectionConfig(
                    new BasicAuthHttpAuthentication(server.username(), server.password()),
                    server.publicUrl().resolve("/.well-known/jmap"),
                    InsecureX509TrustManager.INSTANCE))) {
                var adminSession = adminClient.getSession().get();
                var fromAccountId = adminSession.getPrimaryAccount(FileNodeAccountCapability.class);

                String domainId;
                {
                    var res = adminClient
                            .call(new StalwartDomain.Get(MethodCall.Arg.of(fromAccountId), null, null))
                            .get()
                            .getMain(StalwartDomain.Get.Response.class);
                    assertFalse(res.list().isEmpty());
                    domainId = res.list().get(0).id();
                    assertNotNull(domainId);
                }

                String secondPassword = "correct horse battery staple";
                var accountRes = adminClient
                        .call(new StalwartAccount.Set(
                                MethodCall.Arg.of(fromAccountId),
                                null,
                                MethodCall.Arg.of(Map.of(
                                        "b",
                                        new StalwartAccount.User(
                                                null,
                                                new StalwartUserAccount.Builder()
                                                        .name("second")
                                                        .domainId(domainId)
                                                        .aliases(Map.of())
                                                        .memberGroupIds(Map.of())
                                                        .permissions(new StalwartPermissions.Inherit())
                                                        .quotas(Map.of())
                                                        .roles(new StalwartUserRoles.User())
                                                        .encryptionAtRest(new StalwartEncryptionAtRest.Disabled())
                                                        .credentials(Map.of(
                                                                0,
                                                                new StalwartCredential.Password(
                                                                        new StalwartPasswordCredential.Builder()
                                                                                .allowedIps(Map.of())
                                                                                .secret(secondPassword)
                                                                                .build())))
                                                        .build()))),
                                null,
                                null))
                        .get()
                        .getMain(StalwartAccount.Set.Response.class);
                assertNull(accountRes.notCreated());
                assertNotNull(accountRes.created().get("b"));

                String folderId;
                {
                    var res = adminClient
                            .call(new SetFileNodeCall(
                                    MethodCall.Arg.of(fromAccountId),
                                    null,
                                    MethodCall.Arg.of(Map.of(
                                            "a",
                                            FileNode.builder()
                                                    .nodeType("directory")
                                                    .name("to-copy")
                                                    .created(Instant.now())
                                                    .build())),
                                    null,
                                    null))
                            .get()
                            .getMain(SetFileNodeResponse.class);
                    assertNull(res.notCreated());
                    folderId = res.created().get("a").id();
                    assertNotNull(folderId);
                }

                try (var secondClient = new JmapClient(new ConnectionConfig(
                        new BasicAuthHttpAuthentication("second@stalwart.test", secondPassword),
                        server.publicUrl().resolve("/.well-known/jmap"),
                        InsecureX509TrustManager.INSTANCE))) {
                    var secondSession = secondClient.getSession().get();
                    var toAccountId = secondSession.getPrimaryAccount(FileNodeAccountCapability.class);

                    var res = adminClient
                            .call(new CopyFileNodeCall(
                                    MethodCall.Arg.of(fromAccountId),
                                    null,
                                    MethodCall.Arg.of(toAccountId),
                                    null,
                                    MethodCall.Arg.of(
                                            Map.of(folderId, FileNode.builder().build())),
                                    null,
                                    null))
                            .get()
                            .getMain(CopyFileNodeResponse.class);
                    assertNull(res.notCreated());
                    assertNotNull(res.created());
                    assertEquals(1, res.created().size());
                    String copiedId = res.created().values().iterator().next().id();

                    var getRes = secondClient
                            .call(new GetFileNodeCall(
                                    MethodCall.Arg.of(toAccountId), MethodCall.Arg.of(List.of(copiedId)), null))
                            .get()
                            .getMain(GetFileNodeResponse.class);
                    assertEquals(1, getRes.list().size());
                    assertEquals("to-copy", getRes.list().get(0).name());
                }
            }
        }
    }
}
