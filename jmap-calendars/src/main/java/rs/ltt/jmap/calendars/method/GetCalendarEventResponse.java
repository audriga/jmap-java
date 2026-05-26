package rs.ltt.jmap.calendars.method;

import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.calendars.entity.CalendarEvent;
import rs.ltt.jmap.common.method.response.standard.GetMethodResponse;

@JmapMethod("CalendarEvent/get")
public class GetCalendarEventResponse extends GetMethodResponse<CalendarEvent> {
    public GetCalendarEventResponse(String accountId, String state, String[] notFound, CalendarEvent[] list) {
        super(accountId, state, notFound, list);
    }
}
