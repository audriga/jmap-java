package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.ParticipantIdentity;
import com.audriga.jmap.common.Request;
import com.audriga.jmap.common.method.call.standard.GetMethodCall;
import lombok.NonNull;

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
