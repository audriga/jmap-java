package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.ParticipantIdentity;
import com.audriga.jmap.common.method.response.standard.ChangesMethodResponse;

@JmapMethod("ParticipantIdentity/changes")
public class ChangesParticipantIdentityResponse extends ChangesMethodResponse<ParticipantIdentity> {
    public ChangesParticipantIdentityResponse(
            String accountId,
            String oldState,
            String newState,
            boolean hasMoreChanges,
            String[] created,
            String[] updated,
            String[] destroyed) {
        super(accountId, oldState, newState, hasMoreChanges, created, updated, destroyed);
    }
}
