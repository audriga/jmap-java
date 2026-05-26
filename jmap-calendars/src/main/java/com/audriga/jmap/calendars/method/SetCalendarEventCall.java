package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.CalendarEvent;
import com.audriga.jmap.common.Request;
import com.audriga.jmap.common.method.call.standard.SetMethodCall;
import java.util.Map;
import lombok.NonNull;

@JmapMethod("CalendarEvent/set")
public class SetCalendarEventCall extends SetMethodCall<CalendarEvent> {
    private Boolean sendSchedulingMessages;

    @lombok.Builder
    public SetCalendarEventCall(
            @NonNull String accountId,
            String ifInState,
            Map<String, CalendarEvent> create,
            Map<String, Map<String, Object>> update,
            String[] destroy,
            Request.Invocation.ResultReference destroyReference,
            Boolean sendSchedulingMessages) {
        super(accountId, ifInState, create, update, destroy, destroyReference);
        this.sendSchedulingMessages = sendSchedulingMessages;
    }
}
