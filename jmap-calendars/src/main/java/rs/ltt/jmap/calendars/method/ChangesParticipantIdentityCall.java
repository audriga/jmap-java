package rs.ltt.jmap.calendars.method;

import lombok.NonNull;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.calendars.entity.ParticipantIdentity;
import rs.ltt.jmap.common.method.call.standard.ChangesMethodCall;

@JmapMethod("ParticipantIdentity/changes")
public class ChangesParticipantIdentityCall extends ChangesMethodCall<ParticipantIdentity> {
    public ChangesParticipantIdentityCall(@NonNull String accountId, @NonNull String sinceState, Long maxChanges) {
        super(accountId, sinceState, maxChanges);
    }
}
