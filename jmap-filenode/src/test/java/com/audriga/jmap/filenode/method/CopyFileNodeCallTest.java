package com.audriga.jmap.filenode.method;

import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.filenode.entity.FileNode;
import com.audriga.jmap.gson.GsonUtils;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

// FileNode/copy needs two separate accounts to actually run against a server, see
// FileNodeIT.copy for why that is not tested live. This only checks that the call and
// response classes serialize and deserialize correctly on their own.
public class CopyFileNodeCallTest {

    @Test
    public void copyCallRoundTrip() {
        CopyFileNodeCall call = CopyFileNodeCall.builder()
                .fromAccountId("accountA")
                .accountId("accountB")
                .create(Map.of(
                        "a",
                        FileNode.builder()
                                .type("Directory")
                                .name("copied-folder")
                                .created(Instant.now())
                                .build()))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(call);
        CopyFileNodeCall parsed = GsonUtils.REGULAR_GSON.fromJson(json, CopyFileNodeCall.class);

        Assertions.assertEquals("accountA", MethodCall.Arg.unwrapValue(parsed.fromAccountId()));
        Assertions.assertEquals("accountB", MethodCall.Arg.unwrapValue(parsed.accountId()));
        Assertions.assertEquals(
                "copied-folder",
                MethodCall.Arg.unwrapValue(parsed.create()).get("a").name());
    }

    @Test
    public void copyResponseRoundTrip() {
        CopyFileNodeResponse response = CopyFileNodeResponse.builder()
                .fromAccountId("accountA")
                .accountId("accountB")
                .newState("1")
                .created(Map.of(
                        "a",
                        FileNode.builder()
                                .id("new-id")
                                .type("Directory")
                                .name("copied-folder")
                                .created(Instant.now())
                                .build()))
                .build();

        String json = GsonUtils.REGULAR_GSON.toJson(response);
        CopyFileNodeResponse parsed = GsonUtils.REGULAR_GSON.fromJson(json, CopyFileNodeResponse.class);

        Assertions.assertEquals("accountA", parsed.fromAccountId());
        Assertions.assertEquals("accountB", parsed.accountId());
        Assertions.assertEquals("new-id", parsed.created().get("a").id());
    }
}
