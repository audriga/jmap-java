package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.calendars.entity.Calendar;
import com.audriga.jmap.common.method.call.standard.ChangesMethodCall;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("Calendar/changes")
@RecordBuilder
public record ChangesCalendarCall(
        @NonNull Arg<String> accountId,
        @NonNull Arg<String> sinceState,
        @Nullable Arg<Long> maxChanges) implements ChangesMethodCall<Calendar> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends ChangesCalendarCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
