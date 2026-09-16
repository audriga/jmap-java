package com.audriga.jmap.sieve.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.QueryMethodResponse;
import com.audriga.jmap.sieve.entity.SieveScript;
import java.util.List;
import org.jspecify.annotations.Nullable;

@JmapMethod("SieveScript/query")
@RecordBuilder
public record QuerySieveScriptResponse(
        String accountId,
        String queryState,
        boolean canCalculateChanges,
        Long position,
        List<String> ids,
        @Nullable Long total,
        @Nullable Long limit)
        implements QueryMethodResponse<SieveScript> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QuerySieveScriptResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
