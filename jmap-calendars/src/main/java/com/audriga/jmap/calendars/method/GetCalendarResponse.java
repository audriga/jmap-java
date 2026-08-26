package com.audriga.jmap.calendars.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.calendars.entity.Calendar;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;
import java.util.List;

@JmapMethod("Calendar/get")
@RecordBuilder
public record GetCalendarResponse(String accountId, String state, List<Calendar> list, List<String> notFound)
        implements GetMethodResponse<Calendar> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetCalendarResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
