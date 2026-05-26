package rs.ltt.jmap.calendars.method;

import lombok.NonNull;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.calendars.entity.Calendar;
import rs.ltt.jmap.common.method.call.standard.ChangesMethodCall;

@JmapMethod("Calendar/changes")
public class ChangesCalendarCall extends ChangesMethodCall<Calendar> {
    public ChangesCalendarCall(@NonNull String accountId, @NonNull String sinceState, Long maxChanges) {
        super(accountId, sinceState, maxChanges);
    }
}
