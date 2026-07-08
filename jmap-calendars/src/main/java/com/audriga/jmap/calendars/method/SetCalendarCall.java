package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.Calendar;
import com.audriga.jmap.common.Request;
import com.audriga.jmap.common.method.call.standard.SetMethodCall;
import java.util.Map;
import lombok.NonNull;
import org.jspecify.annotations.Nullable;

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
