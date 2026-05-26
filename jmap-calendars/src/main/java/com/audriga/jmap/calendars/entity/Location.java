package com.audriga.jmap.calendars.entity;

import com.audriga.jmap.annotation.Type;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@Type
public record Location(
        @Nullable String name,
        @Nullable Set<String> locationTypes,
        @Nullable URI coordinates,
        @Nullable Map<String, CalendarLink> links) {}
