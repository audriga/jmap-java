package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.calendars.entity.CalendarEvent;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@JmapMethod("CalendarEvent/set")
@RecordBuilder
public record SetCalendarEventResponse(
        String accountId,
        @Nullable String oldState,
        String newState,
        @Nullable Map<String, CalendarEvent> created,
        @Nullable Map<String, CalendarEvent> updated,
        @Nullable List<String> destroyed,
        @Nullable Map<String, SetError> notCreated,
        @Nullable Map<String, SetError> notUpdated,
        @Nullable Map<String, SetError> notDestroyed)
        implements SetMethodResponse<CalendarEvent> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends SetCalendarEventResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
