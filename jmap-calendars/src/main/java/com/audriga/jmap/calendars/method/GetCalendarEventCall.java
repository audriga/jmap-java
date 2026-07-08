package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.CalendarEvent;
import com.audriga.jmap.common.Request;
import com.audriga.jmap.common.method.call.standard.GetMethodCall;
import lombok.Builder;
import lombok.NonNull;

@JmapMethod("CalendarEvent/get")
public class GetCalendarEventCall extends GetMethodCall<CalendarEvent> {
    @Builder
    public GetCalendarEventCall(
            @NonNull String accountId,
            String[] ids,
            String[] properties,
            Request.Invocation.ResultReference idsReference) {
        super(accountId, ids, properties, idsReference);
    }
}
