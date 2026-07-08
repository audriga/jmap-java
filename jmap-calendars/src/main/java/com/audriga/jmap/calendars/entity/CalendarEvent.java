package com.audriga.jmap.calendars.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Immutable;
import com.audriga.jmap.annotation.ServerSet;
import com.audriga.jmap.annotation.Type;
import com.audriga.jmap.common.DateTimePeriod;
import com.audriga.jmap.common.entity.Identifiable;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
@Type("Event")
public record CalendarEvent(
        // JMAP Additions
        @Immutable @ServerSet String id,
        @Immutable @ServerSet @Nullable String baseEventId,
        Set<String> calendarIds,
        @Default("\"false\"") Boolean isDraft,
        @ServerSet Boolean isOrigin,
        Instant utcStart,
        Instant utcEnd,
        @Default("\"false\"") Boolean useDefaultAlerts,
        // Metadata
        String uid,
        String version,
        @Nullable Map<String, CalendarRelation> relatedTo,
        @Nullable String prodId,
        @Nullable Instant created,
        Instant updated,
        @Default("0") Long sequence,
        // What and Where
        @Default("\"\"") String title,
        @Default("\"\"") String description,
        @Default("\"text/plain\"") String descriptionContentType,
        @Default("false") Boolean showWithoutTime,
        @Nullable Map<String, Location> locations,
        @Nullable String mainLocationId,
        @Nullable Map<String, VirtualLocation> virtualLocations,
        @Nullable Map<String, Link> links,
        @Nullable String locale,
        @Nullable Set<String> keywords,
        @Nullable Set<String> categories,
        @Nullable String color,
        // Recurrence
        @Nullable LocalDateTime recurrenceId,
        @Nullable String recurrenceIdTimeZone,
        @Nullable RecurrenceRule recurrenceRule,
        @Nullable Map<LocalDateTime, Map<String, Object>> recurrenceOverrides,
        // Sharing and Scheduling
        @Default("0") Integer priority,
        @Default("\"busy\"") String freeBusyStatus,
        @Default("\"public\"") String privacy,
        @Nullable URI organizerCalendarAddress,
        @Nullable Map<String, Participant> participants,
        // Alerts
        @Nullable Map<String, Alert> alerts,
        // Time Zone
        @Nullable String timeZone,
        // Event
        LocalDateTime start,
        @Default("\"PT0S\"") DateTimePeriod duration,
        @Nullable String endTimeZone,
        @Default("\"confirmed\"") String status)
        implements Identifiable {}
