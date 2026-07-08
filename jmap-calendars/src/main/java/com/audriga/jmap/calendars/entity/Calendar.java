package com.audriga.jmap.calendars.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Immutable;
import com.audriga.jmap.annotation.ServerSet;
import com.audriga.jmap.common.entity.Identifiable;
import java.util.Map;
import org.jspecify.annotations.Nullable;

public record Calendar(
        @Immutable @ServerSet String id,
        String name,
        @Nullable String description,
        @Nullable String color,
        @Default("0") int sortOrder,
        boolean isSubscribed,
        @Default("true") boolean isVisible,
        @ServerSet Boolean isDefault,
        String includeInAvailability,
        @Nullable Map<String, Alert> defaultAlertsWithTime,
        @Nullable Map<String, Alert> defaultAlertsWithoutTime,
        @Nullable String timeZone,
        @Nullable Map<String, CalendarRights> shareWith,
        @ServerSet CalendarRights myRights)
        implements Identifiable {}
