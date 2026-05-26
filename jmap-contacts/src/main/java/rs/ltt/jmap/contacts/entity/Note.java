package rs.ltt.jmap.contacts.entity;

import java.net.URI;
import java.time.Instant;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Type;

@Type
public record Note(
        String note, @Nullable Instant created, @Nullable Author author) {
    @Type
    public record Author(@Nullable String name, @Nullable URI uri) {}
}
