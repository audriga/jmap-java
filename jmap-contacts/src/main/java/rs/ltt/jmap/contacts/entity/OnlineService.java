package rs.ltt.jmap.contacts.entity;

import java.net.URI;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Type;

@Type
public record OnlineService(
        @Nullable String service,
        @Nullable URI uri,
        @Nullable String user,
        @Nullable Contexts contexts,
        @Nullable Integer pref,
        @Nullable String label) {}
