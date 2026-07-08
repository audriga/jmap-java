package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.Calendar;
import com.audriga.jmap.common.method.response.standard.ChangesMethodResponse;

@JmapMethod("Calendar/changes")
public class ChangesCalendarResponse extends ChangesMethodResponse<Calendar> {
    public ChangesCalendarResponse(
            String accountId,
            String oldState,
            String newState,
            boolean hasMoreChanges,
            String[] created,
            String[] updated,
            String[] destroyed) {
        super(accountId, oldState, newState, hasMoreChanges, created, updated, destroyed);
    }
}
