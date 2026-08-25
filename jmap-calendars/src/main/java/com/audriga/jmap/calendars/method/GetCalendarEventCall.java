package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.CalendarEvent;
import com.audriga.jmap.common.method.ResultReference;
import com.audriga.jmap.common.method.call.standard.AbstractGetMethodCall;
import lombok.NonNull;

@JmapMethod("CalendarEvent/get")
public class GetCalendarEventCall extends AbstractGetMethodCall<CalendarEvent> {
    @lombok.Builder
    public GetCalendarEventCall(
            @NonNull String accountId, String[] ids, String[] properties, ResultReference idsReference) {
        super(accountId, ids, properties, idsReference);
    }
}
