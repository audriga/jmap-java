package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

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
