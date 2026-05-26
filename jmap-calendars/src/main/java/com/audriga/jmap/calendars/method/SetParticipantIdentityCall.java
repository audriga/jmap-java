package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.ParticipantIdentity;
import com.audriga.jmap.common.Request;
import com.audriga.jmap.common.method.call.standard.SetMethodCall;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

@Getter
@JmapMethod("ParticipantIdentity/set")
public class SetParticipantIdentityCall extends SetMethodCall<ParticipantIdentity> {
    private @Nullable String onSuccessSetIsDefault;

    @Builder
    public SetParticipantIdentityCall(
            @NonNull String accountId,
            String ifInState,
            Map<String, ParticipantIdentity> create,
            Map<String, Map<String, Object>> update,
            String[] destroy,
            Request.Invocation.ResultReference destroyReference,
            @Nullable String onSuccessSetIsDefault) {
        super(accountId, ifInState, create, update, destroy, destroyReference);
        this.onSuccessSetIsDefault = onSuccessSetIsDefault;
    }
}
