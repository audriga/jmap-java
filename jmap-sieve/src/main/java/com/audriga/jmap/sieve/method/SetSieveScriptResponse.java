package com.audriga.jmap.sieve.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import com.audriga.jmap.sieve.entity.SieveScript;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@JmapMethod("SieveScript/set")
@RecordBuilder
public record SetSieveScriptResponse(
        String accountId,
        @Nullable String oldState,
        String newState,
        @Nullable Map<String, SieveScript> created,
        @Nullable Map<String, SieveScript> updated,
        @Nullable List<String> destroyed,
        @Nullable Map<String, SetError> notCreated,
        @Nullable Map<String, SetError> notUpdated,
        @Nullable Map<String, SetError> notDestroyed)
        implements SetMethodResponse<SieveScript> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends SetSieveScriptResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
