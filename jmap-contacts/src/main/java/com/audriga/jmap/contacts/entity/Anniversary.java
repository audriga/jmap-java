package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import java.time.Instant;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder(toBuilder = true)
@Type
public record Anniversary(String kind, Date date, @Nullable Address place) {
    @Type("PartialDate")
    public sealed interface Date {}

    @Builder(toBuilder = true)
    @Type
    public record PartialDate(
            @Nullable Integer year,
            @Nullable Integer month,
            @Nullable Integer day,
            @Nullable String calendarScale) implements Date {}

    @Type
    public record Timestamp(Instant utc) implements Date {}
}
