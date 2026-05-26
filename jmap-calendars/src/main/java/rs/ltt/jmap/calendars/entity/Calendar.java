package rs.ltt.jmap.calendars.entity;

import java.util.Map;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Default;
import rs.ltt.jmap.annotation.Immutable;
import rs.ltt.jmap.annotation.ServerSet;
import rs.ltt.jmap.common.entity.Identifiable;

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
