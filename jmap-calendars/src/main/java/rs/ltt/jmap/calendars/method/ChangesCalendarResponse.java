package rs.ltt.jmap.calendars.method;

import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.calendars.entity.Calendar;
import rs.ltt.jmap.common.method.response.standard.ChangesMethodResponse;

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
