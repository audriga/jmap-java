package rs.ltt.jmap.calendars.method;

import java.util.Map;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.calendars.entity.Calendar;
import rs.ltt.jmap.common.entity.SetError;
import rs.ltt.jmap.common.method.response.standard.SetMethodResponse;

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
