package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.ParticipantIdentity;
import com.audriga.jmap.common.method.call.standard.ChangesMethodCall;
import lombok.Builder;
import lombok.NonNull;

@JmapMethod("ParticipantIdentity/changes")
public class ChangesParticipantIdentityCall extends ChangesMethodCall<ParticipantIdentity> {
    @Builder
    public ChangesParticipantIdentityCall(@NonNull String accountId, @NonNull String sinceState, Long maxChanges) {
        super(accountId, sinceState, maxChanges);
    }
}
