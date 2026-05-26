package rs.ltt.jmap.calendars.entity;

import java.net.URI;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Default;
import rs.ltt.jmap.annotation.Type;

@Type
public record Participant(
        @Nullable String name,
        @Nullable String email,
        @Nullable String description,
        @Nullable String descriptionContentType,
        @Nullable URI calendarAddress,
        @Nullable String kind,
        @Nullable Set<String> roles,
        @Default("false") Boolean expectReply,
        @Nullable String sentBy,
        @Nullable Set<URI> delegatedTo,
        @Nullable Set<URI> delegatedFrom,
        @Nullable Set<URI> memberOf,
        @Nullable Map<String, Link> links,
        @Nullable String progress,
        @Nullable Integer percentComplete) {}
