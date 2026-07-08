package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.CalendarEvent;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import java.util.Map;

@JmapMethod("CalendarEvent/set")
public class SetCalendarEventResponse extends SetMethodResponse<CalendarEvent> {
    public SetCalendarEventResponse(
            String accountId,
            String oldState,
            String newState,
            Map<String, CalendarEvent> created,
            Map<String, CalendarEvent> updated,
            String[] destroyed,
            Map<String, SetError> notCreated,
            Map<String, SetError> notUpdated,
            Map<String, SetError> notDestroyed) {
        super(accountId, oldState, newState, created, updated, destroyed, notCreated, notUpdated, notDestroyed);
    }
}
