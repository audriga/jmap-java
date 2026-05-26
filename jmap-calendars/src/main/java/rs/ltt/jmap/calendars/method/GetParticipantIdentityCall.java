package rs.ltt.jmap.calendars.method;

import lombok.NonNull;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.calendars.entity.ParticipantIdentity;
import rs.ltt.jmap.common.Request;
import rs.ltt.jmap.common.method.call.standard.GetMethodCall;

@JmapMethod("ParticipantIdentity/get")
public class GetParticipantIdentityCall extends GetMethodCall<ParticipantIdentity> {
    public GetParticipantIdentityCall(
            @NonNull String accountId,
            String[] ids,
            String[] properties,
            Request.Invocation.ResultReference idsReference) {
        super(accountId, ids, properties, idsReference);
    }
}
