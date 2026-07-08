package com.audriga.jmap.calendars.entity;

import com.audriga.jmap.annotation.Type;
import java.net.URI;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@Type
public record Link(
        URI href,
        @Nullable String contentType,
        @Nullable Long size,
        @Nullable String rel,
        @Nullable Set<String> display,
        @Nullable String title) {}
