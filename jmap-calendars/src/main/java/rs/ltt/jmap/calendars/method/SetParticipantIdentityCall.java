package rs.ltt.jmap.calendars.method;

import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.calendars.entity.ParticipantIdentity;
import rs.ltt.jmap.common.Request;
import rs.ltt.jmap.common.method.call.standard.SetMethodCall;

@Getter
@JmapMethod("ParticipantIdentity/set")
public class SetParticipantIdentityCall extends SetMethodCall<ParticipantIdentity> {
    private @Nullable String onSuccessSetIsDefault;

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
