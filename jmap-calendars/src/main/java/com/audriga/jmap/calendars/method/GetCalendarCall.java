package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.Calendar;
import com.audriga.jmap.common.method.ResultReference;
import com.audriga.jmap.common.method.call.standard.AbstractGetMethodCall;
import lombok.NonNull;

@JmapMethod("Calendar/get")
public class GetCalendarCall extends AbstractGetMethodCall<Calendar> {
    @lombok.Builder
    public GetCalendarCall(@NonNull String accountId, String[] ids, String[] properties, ResultReference idsReference) {
        super(accountId, ids, properties, idsReference);
    }
}
