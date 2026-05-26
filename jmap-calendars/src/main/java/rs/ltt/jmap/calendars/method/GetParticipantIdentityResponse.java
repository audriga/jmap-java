package rs.ltt.jmap.calendars.method;

import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.calendars.entity.ParticipantIdentity;
import rs.ltt.jmap.common.method.response.standard.GetMethodResponse;

@JmapMethod("ParticipantIdentity/get")
public class GetParticipantIdentityResponse extends GetMethodResponse<ParticipantIdentity> {
    public GetParticipantIdentityResponse(
            String accountId, String state, String[] notFound, ParticipantIdentity[] list) {
        super(accountId, state, notFound, list);
    }
}
