package rs.ltt.jmap.calendars.entity;

import java.net.URI;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Type;

@Type
public record Link(
        URI href,
        @Nullable String contentType,
        @Nullable Long size,
        @Nullable String rel,
        @Nullable Set<String> display,
        @Nullable String title) {}
