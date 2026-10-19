package com.audriga.jmap.tasks.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Immutable;
import com.audriga.jmap.annotation.JmapEntity;
import com.audriga.jmap.annotation.ServerSet;
import com.audriga.jmap.annotation.Type;
import com.audriga.jmap.calendars.entity.Alert;
import com.audriga.jmap.calendars.entity.CalendarLink;
import com.audriga.jmap.calendars.entity.CalendarRelation;
import com.audriga.jmap.calendars.entity.CalendarTimeZone;
import com.audriga.jmap.calendars.entity.Location;
import com.audriga.jmap.calendars.entity.Participant;
import com.audriga.jmap.calendars.entity.RecurrenceRule;
import com.audriga.jmap.calendars.entity.VirtualLocation;
import com.audriga.jmap.common.DateTimePeriod;
import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.tasks.method.TaskFilterCondition;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@JmapEntity(filterCondition = TaskFilterCondition.class)
@lombok.Builder(toBuilder = true)
@Type("Task")
public record Task(
        // JMAP Additions
        @Immutable @ServerSet String id,
        @Nullable String taskListId,
        @Default("\"false\"") Boolean isDraft,
        @Nullable Instant utcStart,
        @Nullable Instant utcDue,
        // Metadata
        String uid,
        @Nullable Map<String, CalendarRelation> relatedTo,
        @Nullable String prodId,
        @Nullable @ServerSet Instant created,
        @Nullable @ServerSet Instant updated,
        @Default("0") Long sequence,
        @Nullable String method,
        // What and Where
        @Default("\"\"") String title,
        @Default("\"\"") String description,
        @Default("\"text/plain\"") String descriptionContentType,
        @Default("false") Boolean showWithoutTime,
        @Nullable Map<String, Location> locations,
        @Nullable Map<String, VirtualLocation> virtualLocations,
        @Nullable Map<String, CalendarLink> links,
        @Nullable String locale,
        @Nullable Set<String> keywords,
        @Nullable Set<String> categories,
        @Nullable String color,
        // Recurrence
        @Nullable List<RecurrenceRule> recurrenceRules,
        @Nullable List<RecurrenceRule> excludedRecurrenceRules,
        @Nullable LocalDateTime recurrenceId,
        @Nullable ZoneId recurrenceIdTimeZone,
        @Nullable Map<LocalDateTime, Map<String, Object>> recurrenceOverrides,
        @Nullable Map<String, Map<String, Object>> localizations,
        @Nullable Map<String, CalendarTimeZone> timeZones,
        // Sharing and Scheduling
        @Default("0") Long priority,
        @Default("\"public\"") String privacy,
        @Nullable String sentBy,
        @Nullable Map<String, String> replyTo,
        @Nullable Map<String, Participant> participants,
        @Nullable String requestStatus,
        @Default("\"busy\"") String freeBusyStatus,
        // Alerts
        @Default("\"false\"") Boolean useDefaultAlerts,
        @Nullable Map<String, Alert> alerts,
        // Time Zone
        @Nullable ZoneId timeZone,
        // Task
        @Nullable LocalDateTime start,
        @Nullable LocalDateTime due,
        @Nullable DateTimePeriod estimatedDuration,
        @Default("0") Long percentComplete,
        @Default("\"needs-action\"") String progress,
        @Nullable Instant progressUpdated,
        @Default("\"confirmed\"") String status,
        @Nullable Long sortOrder,
        @Nullable String sensitivity)
        implements Identifiable {}
