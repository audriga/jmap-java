package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.calendars.entity.Calendar;
import com.audriga.jmap.common.method.call.standard.SetMethodCall;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("Calendar/set")
@RecordBuilder
public record SetCalendarCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<String> ifInState,
        @Nullable Arg<Map<String, Calendar>> create,
        @Nullable Arg<Map<String, Map<String, Object>>> update,
        @Nullable Arg<List<String>> destroy,
        @Default("false") @Nullable Boolean onDestroyRemoveEvents,
        @Nullable String onSuccessSetIsDefault)
        implements SetMethodCall<Calendar> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends SetCalendarCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
