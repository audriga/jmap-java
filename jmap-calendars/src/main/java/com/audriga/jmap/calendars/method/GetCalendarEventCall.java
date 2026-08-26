package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.calendars.entity.CalendarEvent;
import com.audriga.jmap.common.method.call.standard.GetMethodCall;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("CalendarEvent/get")
@RecordBuilder
public record GetCalendarEventCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<List<String>> ids,
        @Nullable Arg<List<String>> properties) implements GetMethodCall<CalendarEvent> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetCalendarEventCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
