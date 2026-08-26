package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.calendars.entity.ParticipantIdentity;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;
import java.util.List;

@JmapMethod("ParticipantIdentity/get")
@RecordBuilder
public record GetParticipantIdentityResponse(
        String accountId, String state, List<ParticipantIdentity> list, List<String> notFound)
        implements GetMethodResponse<ParticipantIdentity> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetParticipantIdentityResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
