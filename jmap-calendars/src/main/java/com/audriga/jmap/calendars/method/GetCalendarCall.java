package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.calendars.entity.Calendar;
import com.audriga.jmap.common.Request;
import com.audriga.jmap.common.method.call.standard.GetMethodCall;
import lombok.NonNull;

@JmapMethod("Calendar/get")
public class GetCalendarCall extends GetMethodCall<Calendar> {
    public GetCalendarCall(
            @NonNull String accountId,
            String[] ids,
            String[] properties,
            Request.Invocation.ResultReference idsReference) {
        super(accountId, ids, properties, idsReference);
    }
}
