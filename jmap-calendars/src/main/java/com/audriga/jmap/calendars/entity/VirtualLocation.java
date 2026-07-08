package com.audriga.jmap.calendars.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Type;
import java.net.URI;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@Type
public record VirtualLocation(
        @Default("\"\"") String name, URI uri, @Nullable Set<String> features) {}
