package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.calendars.entity.ParticipantIdentity;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@JmapMethod("ParticipantIdentity/set")
@RecordBuilder
public record SetParticipantIdentityResponse(
        String accountId,
        @Nullable String oldState,
        String newState,
        @Nullable Map<String, ParticipantIdentity> created,
        @Nullable Map<String, ParticipantIdentity> updated,
        @Nullable List<String> destroyed,
        @Nullable Map<String, SetError> notCreated,
        @Nullable Map<String, SetError> notUpdated,
        @Nullable Map<String, SetError> notDestroyed)
        implements SetMethodResponse<ParticipantIdentity> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends SetParticipantIdentityResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
