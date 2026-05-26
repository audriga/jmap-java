package rs.ltt.jmap.calendars.method;

import java.util.Map;
import lombok.NonNull;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.calendars.entity.CalendarEvent;
import rs.ltt.jmap.common.Request;
import rs.ltt.jmap.common.method.call.standard.SetMethodCall;

@JmapMethod("CalendarEvent/set")
public class SetCalendarEventCall extends SetMethodCall<CalendarEvent> {
    private Boolean sendSchedulingMessages;

    public SetCalendarEventCall(
            @NonNull String accountId,
            String ifInState,
            Map<String, CalendarEvent> create,
            Map<String, Map<String, Object>> update,
            String[] destroy,
            Request.Invocation.ResultReference destroyReference,
            Boolean sendSchedulingMessages) {
        super(accountId, ifInState, create, update, destroy, destroyReference);
        this.sendSchedulingMessages = sendSchedulingMessages;
    }
}
