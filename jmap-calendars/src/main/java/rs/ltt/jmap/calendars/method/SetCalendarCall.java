package rs.ltt.jmap.calendars.method;

import java.util.Map;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.calendars.entity.Calendar;
import rs.ltt.jmap.common.Request;
import rs.ltt.jmap.common.method.call.standard.SetMethodCall;

@JmapMethod("Calendar/set")
public class SetCalendarCall extends SetMethodCall<Calendar> {
    private boolean onDestroyRemoveEvents;
    private @Nullable String onSuccessSetIsDefault;

    public SetCalendarCall(
            @NonNull String accountId,
            String ifInState,
            Map<String, Calendar> create,
            Map<String, Map<String, Object>> update,
            String[] destroy,
            Request.Invocation.ResultReference destroyReference,
            boolean onDestroyRemoveEvents,
            @Nullable String onSuccessSetIsDefault) {
        super(accountId, ifInState, create, update, destroy, destroyReference);
        this.onDestroyRemoveEvents = onDestroyRemoveEvents;
        this.onSuccessSetIsDefault = onSuccessSetIsDefault;
    }
}
