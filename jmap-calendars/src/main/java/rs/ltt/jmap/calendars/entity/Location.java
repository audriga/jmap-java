package rs.ltt.jmap.calendars.entity;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Type;

@Type
public record Location(
        @Nullable String name,
        @Nullable Set<String> locationTypes,
        @Nullable URI coordinates,
        @Nullable Map<String, Link> links) {}
