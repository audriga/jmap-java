package com.audriga.jmap.sieve.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;
import com.audriga.jmap.sieve.entity.SieveScript;
import java.util.List;

@JmapMethod("SieveScript/get")
@RecordBuilder
public record GetSieveScriptResponse(String accountId, String state, List<SieveScript> list, List<String> notFound)
        implements GetMethodResponse<SieveScript> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetSieveScriptResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
