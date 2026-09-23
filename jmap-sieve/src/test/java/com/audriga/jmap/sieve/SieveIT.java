package com.audriga.jmap.sieve;

import static org.junit.jupiter.api.Assertions.*;

import com.audriga.jmap.client.ConnectionConfig;
import com.audriga.jmap.client.JmapClient;
import com.audriga.jmap.client.blob.Uploadable;
import com.audriga.jmap.client.http.BasicAuthHttpAuthentication;
import com.audriga.jmap.sieve.entity.SieveScript;
import com.audriga.jmap.sieve.method.GetSieveScriptCall;
import com.audriga.jmap.sieve.method.GetSieveScriptResponse;
import com.audriga.jmap.sieve.method.QuerySieveScriptCall;
import com.audriga.jmap.sieve.method.QuerySieveScriptResponse;
import com.audriga.jmap.sieve.method.SetSieveScriptCall;
import com.audriga.jmap.sieve.method.SetSieveScriptResponse;
import com.audriga.jmap.sieve.method.ValidateSieveScriptCall;
import com.audriga.jmap.sieve.method.ValidateSieveScriptResponse;
import com.audriga.jmap.stalwart.StalwartAccount;
import com.audriga.jmap.stalwart.StalwartCredential;
import com.audriga.jmap.stalwart.StalwartDomain;
import com.audriga.jmap.stalwart.StalwartEncryptionAtRest;
import com.audriga.jmap.stalwart.StalwartJmapAccountCapability;
import com.audriga.jmap.stalwart.StalwartPasswordCredential;
import com.audriga.jmap.stalwart.StalwartPermissions;
import com.audriga.jmap.stalwart.StalwartUserAccount;
import com.audriga.jmap.stalwart.StalwartUserRoles;
import com.google.common.net.MediaType;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.Test;

class SieveIT {
    private static final String VALID_SCRIPT = "require \"fileinto\";\r\nfileinto \"Archive\";\r\n";
    private static final String INVALID_SCRIPT = "this is not a valid sieve script !!!";

    @Test
    void adminCanGetSieveScripts() throws ExecutionException, InterruptedException {
        try (var server = StalwartContainer.latest()) {
            server.start();
            try (var client = new JmapClient(new ConnectionConfig(
                    new BasicAuthHttpAuthentication(server.username(), server.password()),
                    server.publicUrl().resolve("/.well-known/jmap"),
                    InsecureX509TrustManager.INSTANCE))) {
                var session = client.getSession().get();
                var accountId = session.getPrimaryAccount(SieveAccountCapability.class);
                assertNotNull(accountId);

                var res = client.call(GetSieveScriptCall.builder()
                                .accountId(accountId)
                                .build())
                        .get()
                        .getMain(GetSieveScriptResponse.class);
                assertNotNull(res.list());
                assertTrue(res.list().isEmpty());
            }
        }
    }

    @Test
    void adminCanValidateSieveScripts() throws ExecutionException, InterruptedException {
        try (var server = StalwartContainer.latest()) {
            server.start();
            try (var client = new JmapClient(new ConnectionConfig(
                    new BasicAuthHttpAuthentication(server.username(), server.password()),
                    server.publicUrl().resolve("/.well-known/jmap"),
                    InsecureX509TrustManager.INSTANCE))) {
                var session = client.getSession().get();
                var accountId = session.getPrimaryAccount(SieveAccountCapability.class);
                assertNotNull(accountId);

                var validBlobId = client.upload(accountId, uploadableOf(VALID_SCRIPT), null)
                        .get()
                        .blobId();
                var validRes = client.call(ValidateSieveScriptCall.builder()
                                .accountId(accountId)
                                .blobId(validBlobId)
                                .build())
                        .get()
                        .getMain(ValidateSieveScriptResponse.class);
                // Gson deserializes a JSON "null" as JsonNull.INSTANCE here, not Java null.
                assertTrue(
                        validRes.error() == null || validRes.error().isJsonNull(),
                        "expected a syntactically valid script to have no error");

                var invalidBlobId = client.upload(accountId, uploadableOf(INVALID_SCRIPT), null)
                        .get()
                        .blobId();
                var invalidRes = client.call(ValidateSieveScriptCall.builder()
                                .accountId(accountId)
                                .blobId(invalidBlobId)
                                .build())
                        .get()
                        .getMain(ValidateSieveScriptResponse.class);
                assertTrue(
                        invalidRes.error() != null && !invalidRes.error().isJsonNull(),
                        "expected a syntactically invalid script to report an error");
            }
        }
    }

    @Test
    void adminCanCreateQueryAndDestroySieveScript() throws ExecutionException, InterruptedException {
        try (var server = StalwartContainer.latest()) {
            server.start();
            try (var client = new JmapClient(new ConnectionConfig(
                    new BasicAuthHttpAuthentication(server.username(), server.password()),
                    server.publicUrl().resolve("/.well-known/jmap"),
                    InsecureX509TrustManager.INSTANCE))) {
                var session = client.getSession().get();
                var accountId = session.getPrimaryAccount(SieveAccountCapability.class);
                assertNotNull(accountId);

                var blobId = client.upload(accountId, uploadableOf(VALID_SCRIPT), null)
                        .get()
                        .blobId();

                var script = SieveScript.builder()
                        .name("integration-test-script")
                        .blobId(blobId)
                        .isActive(false)
                        .build();
                var setRes = client.call(SetSieveScriptCall.builder()
                                .accountId(accountId)
                                .create(Map.of("s1", script))
                                .build())
                        .get()
                        .getMain(SetSieveScriptResponse.class);
                assertNotNull(setRes.created(), "expected the script to be created, notCreated=" + setRes.notCreated());
                var createdId = setRes.created().get("s1").id();
                assertNotNull(createdId);

                var queryRes = client.call(QuerySieveScriptCall.builder()
                                .accountId(accountId)
                                .build())
                        .get()
                        .getMain(QuerySieveScriptResponse.class);
                assertTrue(queryRes.ids().contains(createdId), "expected the created script to show up in a query");

                var destroyRes = client.call(SetSieveScriptCall.builder()
                                .accountId(accountId)
                                .destroy(List.of(createdId))
                                .build())
                        .get()
                        .getMain(SetSieveScriptResponse.class);
                assertNotNull(destroyRes.destroyed(), "expected the script to be destroyed");
                assertTrue(destroyRes.destroyed().contains(createdId));
            }
        }
    }

    // Creates two scripts, activates the first via onSuccessActivateScript at creation time,
    // activates the second standalone (no create/update/destroy in the same call), then
    // deactivates it standalone too via onSuccessDeactivateScript.
    @Test
    void adminCanActivateAndDeactivateSieveScriptsViaOnSuccessArguments() throws Exception {
        try (var server = StalwartContainer.latest()) {
            server.start();
            try (var client = new JmapClient(new ConnectionConfig(
                    new BasicAuthHttpAuthentication(server.username(), server.password()),
                    server.publicUrl().resolve("/.well-known/jmap"),
                    InsecureX509TrustManager.INSTANCE))) {
                var session = client.getSession().get();
                var accountId = session.getPrimaryAccount(SieveAccountCapability.class);

                var blobId1 = client.upload(accountId, uploadableOf(VALID_SCRIPT), null)
                        .get()
                        .blobId();
                var script1 = SieveScript.builder().name("t1").blobId(blobId1).build();
                var setRes1 = client.call(SetSieveScriptCall.builder()
                                .accountId(accountId)
                                .create(Map.of("s1", script1))
                                .onSuccessActivateScript("#s1")
                                .build())
                        .get()
                        .getMain(SetSieveScriptResponse.class);
                assertNotNull(setRes1.created(), "expected script 1 to be created");
                assertTrue(
                        setRes1.created().get("s1").isActive(),
                        "expected script 1 to be active right after creation via onSuccessActivateScript");

                var blobId2 = client.upload(accountId, uploadableOf(VALID_SCRIPT), null)
                        .get()
                        .blobId();
                var script2 = SieveScript.builder().name("t2").blobId(blobId2).build();
                var setRes2 = client.call(SetSieveScriptCall.builder()
                                .accountId(accountId)
                                .create(Map.of("s2", script2))
                                .build())
                        .get()
                        .getMain(SetSieveScriptResponse.class);
                var secondId = setRes2.created().get("s2").id();

                // Activate script 2 standalone, no create/update/destroy in the same call.
                client.call(SetSieveScriptCall.builder()
                                .accountId(accountId)
                                .onSuccessActivateScript(secondId)
                                .build())
                        .get()
                        .getMain(SetSieveScriptResponse.class);
                var afterActivate = client.call(GetSieveScriptCall.builder()
                                .accountId(accountId)
                                .build())
                        .get()
                        .getMain(GetSieveScriptResponse.class);
                var script2AfterActivate = afterActivate.list().stream()
                        .filter(s -> s.id().equals(secondId))
                        .findFirst()
                        .orElseThrow();
                assertTrue(
                        script2AfterActivate.isActive(),
                        "expected script 2 to be active after standalone onSuccessActivateScript");

                // onSuccessDeactivateScript is a plain boolean, it deactivates whichever script
                // is currently active, it does not take an id.
                client.call(SetSieveScriptCall.builder()
                                .accountId(accountId)
                                .onSuccessDeactivateScript(true)
                                .build())
                        .get()
                        .getMain(SetSieveScriptResponse.class);
                var afterDeactivate = client.call(GetSieveScriptCall.builder()
                                .accountId(accountId)
                                .build())
                        .get()
                        .getMain(GetSieveScriptResponse.class);
                assertTrue(
                        afterDeactivate.list().stream().noneMatch(SieveScript::isActive),
                        "expected no script to be active after onSuccessDeactivateScript(true)");
            }
        }
    }

    // Creates a second, non-admin account and checks that admin can impersonate it to manage
    // its sieve scripts, and that the member itself cannot impersonate anyone else.
    @Test
    void adminCanImpersonateMemberToManageSieveScriptsButMemberCannotImpersonate()
            throws ExecutionException, InterruptedException {
        try (var server = StalwartContainer.latest()) {
            server.start();
            try (var adminClient = new JmapClient(new ConnectionConfig(
                    new BasicAuthHttpAuthentication(server.username(), server.password()),
                    server.publicUrl().resolve("/.well-known/jmap"),
                    InsecureX509TrustManager.INSTANCE))) {
                var adminSession = adminClient.getSession().get();
                var adminAccountId = adminSession.getPrimaryAccount(StalwartJmapAccountCapability.class);
                assertNotNull(adminAccountId);

                var domainQuery = adminClient
                        .call(StalwartDomain.Query.builder()
                                .accountId(adminAccountId)
                                .build())
                        .get()
                        .getMain(StalwartDomain.Query.Response.class);
                var domainGet = adminClient
                        .call(StalwartDomain.Get.builder()
                                .accountId(adminAccountId)
                                .ids(domainQuery.ids())
                                .build())
                        .get()
                        .getMain(StalwartDomain.Get.Response.class);
                var domainId = domainGet.list().stream()
                        .filter(d -> "stalwart.test".equals(d.name()))
                        .map(StalwartDomain::id)
                        .findFirst()
                        .orElseThrow(() -> new AssertionError("expected domain \"stalwart.test\" to already exist"));

                var memberName = "member";
                var memberPassword = "correct-horse-battery-staple-42";
                // Built via the raw constructor, not the generated Builder, so the server-set
                // and defaulted fields (createdAt, emailAddress, usedDiskQuota, locale) stay
                // null instead of the Builder filling them in.
                var memberAccount = new StalwartUserAccount(
                        Map.of(),
                        null,
                        Map.of(
                                0,
                                new StalwartCredential.Password(
                                        new StalwartPasswordCredential(Map.of(), null, null, null, memberPassword))),
                        null,
                        domainId,
                        null,
                        new StalwartEncryptionAtRest.Disabled(),
                        null,
                        Map.of(),
                        null,
                        memberName,
                        new StalwartPermissions.Inherit(),
                        Map.of(),
                        new StalwartUserRoles.User(),
                        null,
                        null);
                var createRes = adminClient
                        .call(StalwartAccount.Set.builder()
                                .accountId(adminAccountId)
                                .create(Map.of("m", new StalwartAccount.User(null, memberAccount)))
                                .build())
                        .get()
                        .getMain(StalwartAccount.Set.Response.class);
                assertNotNull(
                        createRes.created(),
                        "expected the member account to be created, notCreated=" + createRes.notCreated());

                // Admin impersonates the member via Basic auth "member%admin" and manages that
                // member's own sieve scripts.
                try (var impersonatedClient = new JmapClient(new ConnectionConfig(
                        new BasicAuthHttpAuthentication(memberName + "%" + server.username(), server.password()),
                        server.publicUrl().resolve("/.well-known/jmap"),
                        InsecureX509TrustManager.INSTANCE))) {
                    var memberSession = impersonatedClient.getSession().get();
                    var memberAccountId = memberSession.getPrimaryAccount(SieveAccountCapability.class);
                    assertNotNull(memberAccountId);
                    assertNotEquals(
                            adminAccountId,
                            memberAccountId,
                            "expected impersonation to act as the member's own account, not admin's");

                    var getRes = impersonatedClient
                            .call(GetSieveScriptCall.builder()
                                    .accountId(memberAccountId)
                                    .build())
                            .get()
                            .getMain(GetSieveScriptResponse.class);
                    assertNotNull(getRes.list());
                    assertTrue(getRes.list().isEmpty());
                }

                // The member itself has no admin privileges and cannot impersonate anyone else.
                try (var deniedClient = new JmapClient(new ConnectionConfig(
                        new BasicAuthHttpAuthentication(server.username() + "%" + memberName, memberPassword),
                        server.publicUrl().resolve("/.well-known/jmap"),
                        InsecureX509TrustManager.INSTANCE))) {
                    assertThrows(
                            Exception.class,
                            () -> deniedClient.getSession().get(),
                            "expected a non-admin member to be denied when trying to impersonate another account");
                }
            }
        }
    }

    private static Uploadable uploadableOf(String content) {
        final byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        return new Uploadable() {
            @Override
            public InputStream getInputStream() {
                return new ByteArrayInputStream(bytes);
            }

            @Override
            public MediaType getMediaType() {
                return MediaType.create("application", "sieve");
            }

            @Override
            public long getContentLength() {
                return bytes.length;
            }
        };
    }
}
