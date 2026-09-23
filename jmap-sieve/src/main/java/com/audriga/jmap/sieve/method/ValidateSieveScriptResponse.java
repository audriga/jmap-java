package com.audriga.jmap.sieve.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.method.MethodResponse;
import com.google.gson.JsonElement;
import lombok.Getter;
import org.jspecify.annotations.Nullable;

@JmapMethod("SieveScript/validate")
@Getter
public final class ValidateSieveScriptResponse implements MethodResponse {

    private String accountId;

    // Stalwart returns a structured object here on failure, not a plain string.
    @Nullable
    private JsonElement error;

    @lombok.Builder
    public ValidateSieveScriptResponse(final String accountId, @Nullable final JsonElement error) {
        this.accountId = accountId;
        this.error = error;
    }
}
