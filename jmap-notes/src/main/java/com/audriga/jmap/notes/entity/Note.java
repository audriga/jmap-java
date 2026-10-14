package com.audriga.jmap.notes.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Immutable;
import com.audriga.jmap.annotation.JmapEntity;
import com.audriga.jmap.annotation.ServerSet;
import com.audriga.jmap.annotation.Type;
import com.audriga.jmap.calendars.entity.CalendarLink;
import com.audriga.jmap.calendars.entity.Location;
import com.audriga.jmap.calendars.entity.VirtualLocation;
import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.notes.method.NoteFilterCondition;
import java.time.Instant;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@JmapEntity(filterCondition = NoteFilterCondition.class)
@lombok.Builder(toBuilder = true)
@Type
public record Note(
        @Immutable @ServerSet String id,
        @Nullable String notebookId,
        @Nullable @ServerSet Instant created,
        @Nullable @ServerSet Instant updated,
        @Default("0") Long sequence,
        @Nullable String name,
        @Nullable String body,
        @Nullable String bodyContentType,
        @Nullable Map<String, Location> locations,
        @Nullable Map<String, VirtualLocation> virtualLocations,
        @Nullable Map<String, CalendarLink> links,
        @Nullable Map<String, Boolean> keywords,
        @Nullable Map<String, Boolean> categories,
        @Nullable String color)
        implements Identifiable {}
