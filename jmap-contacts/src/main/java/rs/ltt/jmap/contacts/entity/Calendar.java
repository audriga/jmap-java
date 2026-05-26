package rs.ltt.jmap.contacts.entity;

import java.net.URI;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Type;

@Type
public record Calendar(
        String kind,
        URI uri,
        @Nullable String mediaType,
        @Nullable Contexts contexts,
        @Nullable Integer pref,
        @Nullable String label)
        implements Resource {}
