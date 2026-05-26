package com.audriga.jmap.calendars.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Type;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
@Type
public record Participant(
        @Nullable String name,
        @Nullable String email,
        @Nullable String description,
        @Nullable String descriptionContentType,
        @Nullable URI calendarAddress,
        @Nullable String kind,
        @Nullable Set<String> roles,
        @Default("\"needs-action\"") String participationStatus,
        @Default("false") Boolean expectReply,
        @Nullable String sentBy,
        @Nullable Set<URI> delegatedTo,
        @Nullable Set<URI> delegatedFrom,
        @Nullable Set<URI> memberOf,
        @Nullable Map<String, CalendarLink> links,
        @Nullable String progress,
        @Nullable Integer percentComplete) {}
