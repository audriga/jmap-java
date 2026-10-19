package com.audriga.jmap.calendars.entity;

import com.audriga.jmap.annotation.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@lombok.Builder(toBuilder = true)
@Type("TimeZone")
public record CalendarTimeZone(
        String tzId,
        @Nullable Instant updated,
        @Nullable String url,
        @Nullable Instant validUntil,
        @Nullable List<String> aliases,
        @Nullable List<Rule> standard,
        @Nullable List<Rule> daylight) {
    @Type("TimeZoneRule")
    public record Rule(
            LocalDateTime start,
            String offsetFrom,
            String offsetTo,
            @Nullable RecurrenceRule recurrenceRule,
            @Nullable Map<LocalDateTime, Map<String, Object>> recurrenceOverrides,
            @Nullable Map<String, Boolean> names,
            @Nullable List<String> comments) {}
}
