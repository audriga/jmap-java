package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.calendars.entity.ParticipantIdentity;
import com.audriga.jmap.common.method.call.standard.SetMethodCall;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("ParticipantIdentity/set")
@RecordBuilder
public record SetParticipantIdentityCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<String> ifInState,
        @Nullable Arg<Map<String, ParticipantIdentity>> create,
        @Nullable Arg<Map<String, Map<String, Object>>> update,
        @Nullable Arg<List<String>> destroy,
        @Nullable String onSuccessSetIsDefault)
        implements SetMethodCall<ParticipantIdentity> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends SetParticipantIdentityCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
