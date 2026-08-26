package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.calendars.entity.ParticipantIdentity;
import com.audriga.jmap.common.method.response.standard.ChangesMethodResponse;
import java.util.List;
import org.jspecify.annotations.NonNull;

@JmapMethod("ParticipantIdentity/changes")
@RecordBuilder
public record ChangesParticipantIdentityResponse(
        @NonNull String accountId,
        @NonNull String oldState,
        @NonNull String newState,
        boolean hasMoreChanges,
        @NonNull List<String> created,
        @NonNull List<String> updated,
        @NonNull List<String> destroyed)
        implements ChangesMethodResponse<ParticipantIdentity> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends ChangesParticipantIdentityResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
