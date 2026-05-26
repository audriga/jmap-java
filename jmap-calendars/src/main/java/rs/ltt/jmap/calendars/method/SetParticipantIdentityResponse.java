package rs.ltt.jmap.calendars.method;

import java.util.Map;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.calendars.entity.ParticipantIdentity;
import rs.ltt.jmap.common.entity.SetError;
import rs.ltt.jmap.common.method.response.standard.SetMethodResponse;

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
