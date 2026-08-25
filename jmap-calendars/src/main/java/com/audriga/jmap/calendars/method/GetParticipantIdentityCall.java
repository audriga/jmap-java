package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.ParticipantIdentity;
import com.audriga.jmap.common.method.ResultReference;
import com.audriga.jmap.common.method.call.standard.AbstractGetMethodCall;
import lombok.NonNull;

@JmapMethod("ParticipantIdentity/get")
public class GetParticipantIdentityCall extends AbstractGetMethodCall<ParticipantIdentity> {
    @lombok.Builder
    public GetParticipantIdentityCall(
            @NonNull String accountId, String[] ids, String[] properties, ResultReference idsReference) {
        super(accountId, ids, properties, idsReference);
    }
}
