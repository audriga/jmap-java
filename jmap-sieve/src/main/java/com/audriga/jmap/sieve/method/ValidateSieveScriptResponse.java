package com.audriga.jmap.sieve.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.method.MethodResponse;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

@JmapMethod("SieveScript/validate")
@Getter
public final class ValidateSieveScriptResponse implements MethodResponse {

    private String accountId;

    @Nullable
    private String error;

    @lombok.Builder
    public ValidateSieveScriptResponse(final String accountId, @Nullable final String error) {
        this.accountId = accountId;
        this.error = error;
    }
}
