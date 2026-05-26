package rs.ltt.jmap.contacts.entity;

import java.net.URI;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Type;

@Type
public record SchedulingAddress(
        URI uri,
        @Nullable Contexts contexts,
        @Nullable Integer pref,
        @Nullable String label) {}
