package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.calendars.entity.ParticipantIdentity;
import com.audriga.jmap.common.method.call.standard.GetMethodCall;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("ParticipantIdentity/get")
@RecordBuilder
public record GetParticipantIdentityCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<List<String>> ids,
        @Nullable Arg<List<String>> properties) implements GetMethodCall<ParticipantIdentity> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetParticipantIdentityCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
