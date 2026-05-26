package rs.ltt.jmap.calendars.method;

import java.util.Map;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.calendars.entity.CalendarEvent;
import rs.ltt.jmap.common.entity.SetError;
import rs.ltt.jmap.common.method.response.standard.SetMethodResponse;

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
