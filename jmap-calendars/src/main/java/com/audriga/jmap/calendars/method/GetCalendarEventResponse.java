package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.calendars.entity.CalendarEvent;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;
import java.util.List;

@JmapMethod("CalendarEvent/get")
@RecordBuilder
public record GetCalendarEventResponse(String accountId, String state, List<CalendarEvent> list, List<String> notFound)
        implements GetMethodResponse<CalendarEvent> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetCalendarEventResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
