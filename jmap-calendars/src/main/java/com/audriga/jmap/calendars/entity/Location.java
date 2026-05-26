package com.audriga.jmap.calendars.entity;

import com.audriga.jmap.annotation.Type;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder(toBuilder = true)
@Type
public record Location(
        @Nullable String name,
        @Nullable Set<String> locationTypes,
        @Nullable URI coordinates,
        @Nullable Map<String, CalendarLink> links) {}
