package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.Calendar;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import java.util.Map;

@JmapMethod("Calendar/set")
public class SetCalendarResponse extends SetMethodResponse<Calendar> {
    public SetCalendarResponse(
            String accountId,
            String oldState,
            String newState,
            Map<String, Calendar> created,
            Map<String, Calendar> updated,
            String[] destroyed,
            Map<String, SetError> notCreated,
            Map<String, SetError> notUpdated,
            Map<String, SetError> notDestroyed) {
        super(accountId, oldState, newState, created, updated, destroyed, notCreated, notUpdated, notDestroyed);
    }
}
