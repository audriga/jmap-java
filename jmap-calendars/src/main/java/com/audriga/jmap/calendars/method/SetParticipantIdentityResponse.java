package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.ParticipantIdentity;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import java.util.Map;

@JmapMethod("ParticipantIdentity/set")
public class SetParticipantIdentityResponse extends SetMethodResponse<ParticipantIdentity> {
    public SetParticipantIdentityResponse(
            String accountId,
            String oldState,
            String newState,
            Map<String, ParticipantIdentity> created,
            Map<String, ParticipantIdentity> updated,
            String[] destroyed,
            Map<String, SetError> notCreated,
            Map<String, SetError> notUpdated,
            Map<String, SetError> notDestroyed) {
        super(accountId, oldState, newState, created, updated, destroyed, notCreated, notUpdated, notDestroyed);
    }
}
