package com.audriga.jmap.sieve.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.method.MethodCall;
import lombok.Getter;

@JmapMethod("SieveScript/validate")
@Getter
public final class ValidateSieveScriptCall implements MethodCall {

    private String accountId;
    private String blobId;

    @lombok.Builder
    public ValidateSieveScriptCall(final String accountId, final String blobId) {
        this.accountId = accountId;
        this.blobId = blobId;
    }
}
