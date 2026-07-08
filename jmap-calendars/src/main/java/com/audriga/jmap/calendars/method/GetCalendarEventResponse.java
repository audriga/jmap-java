package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.CalendarEvent;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;

@JmapMethod("CalendarEvent/get")
public class GetCalendarEventResponse extends GetMethodResponse<CalendarEvent> {
    public GetCalendarEventResponse(String accountId, String state, String[] notFound, CalendarEvent[] list) {
        super(accountId, state, notFound, list);
    }
}
