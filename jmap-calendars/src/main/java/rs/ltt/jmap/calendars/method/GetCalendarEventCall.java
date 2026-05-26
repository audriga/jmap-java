package rs.ltt.jmap.calendars.method;

import lombok.Builder;
import lombok.NonNull;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.calendars.entity.CalendarEvent;
import rs.ltt.jmap.common.Request;
import rs.ltt.jmap.common.method.call.standard.GetMethodCall;

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
