package com.audriga.jmap.tasks.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Immutable;
import com.audriga.jmap.annotation.JmapEntity;
import com.audriga.jmap.annotation.ServerSet;
import com.audriga.jmap.annotation.Type;
import com.audriga.jmap.calendars.entity.Alert;
import com.audriga.jmap.calendars.entity.CalendarRights;
import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.tasks.method.TaskListFilterCondition;
import java.time.ZoneId;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@JmapEntity(filterCondition = TaskListFilterCondition.class)
@lombok.Builder(toBuilder = true)
@Type
public record TaskList(
        @Immutable @ServerSet String id,
        @Nullable String name,
        @Nullable String description,
        @Nullable String color,
        @Default("0") int sortOrder,
        @Nullable Boolean isSubscribed,
        @Default("true") boolean isVisible,
        @ServerSet Boolean isDefault,
        @Nullable String includeInAvailability,
        @Nullable Map<String, Alert> defaultAlertsWithTime,
        @Nullable Map<String, Alert> defaultAlertsWithoutTime,
        @Nullable ZoneId timeZone,
        @Nullable Map<String, CalendarRights> shareWith,
        @ServerSet CalendarRights myRights)
        implements Identifiable {}
