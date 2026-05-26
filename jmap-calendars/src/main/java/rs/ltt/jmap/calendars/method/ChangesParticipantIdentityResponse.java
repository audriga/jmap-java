package rs.ltt.jmap.calendars.method;

import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.calendars.entity.ParticipantIdentity;
import rs.ltt.jmap.common.method.response.standard.ChangesMethodResponse;

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
