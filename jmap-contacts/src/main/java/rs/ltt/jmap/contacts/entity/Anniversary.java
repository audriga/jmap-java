package rs.ltt.jmap.contacts.entity;

import java.time.Instant;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Type;

@Type
public record Anniversary(String kind, Date date, @Nullable Address place) {
    @Type("PartialDate")
    public sealed interface Date {}

    @Type
    public record PartialDate(
            @Nullable Integer year,
            @Nullable Integer month,
            @Nullable Integer day,
            @Nullable String calendarScale) implements Date {}

    @Type
    public record Timestamp(Instant utc) implements Date {}
}
