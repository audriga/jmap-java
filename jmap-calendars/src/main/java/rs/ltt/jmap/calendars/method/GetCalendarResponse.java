package rs.ltt.jmap.calendars.method;

import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.calendars.entity.Calendar;
import rs.ltt.jmap.common.method.response.standard.GetMethodResponse;

@JmapMethod("Calendar/get")
public class GetCalendarResponse extends GetMethodResponse<Calendar> {
    public GetCalendarResponse(String accountId, String state, String[] notFound, Calendar[] list) {
        super(accountId, state, notFound, list);
    }
}
