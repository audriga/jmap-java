package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.ParticipantIdentity;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;

@JmapMethod("ParticipantIdentity/get")
public class GetParticipantIdentityResponse extends GetMethodResponse<ParticipantIdentity> {
    public GetParticipantIdentityResponse(
            String accountId, String state, String[] notFound, ParticipantIdentity[] list) {
        super(accountId, state, notFound, list);
    }
}
