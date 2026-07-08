package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.Calendar;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;

@JmapMethod("Calendar/get")
public class GetCalendarResponse extends GetMethodResponse<Calendar> {
    public GetCalendarResponse(String accountId, String state, String[] notFound, Calendar[] list) {
        super(accountId, state, notFound, list);
    }
}
