package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.Calendar;
import com.audriga.jmap.common.method.call.standard.ChangesMethodCall;
import lombok.NonNull;

@JmapMethod("Calendar/changes")
public class ChangesCalendarCall extends ChangesMethodCall<Calendar> {
    @lombok.Builder
    public ChangesCalendarCall(@NonNull String accountId, @NonNull String sinceState, Long maxChanges) {
        super(accountId, sinceState, maxChanges);
    }
}
