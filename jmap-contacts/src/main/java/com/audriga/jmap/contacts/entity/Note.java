package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import java.net.URI;
import java.time.Instant;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder(toBuilder = true)
@Type
public record Note(
        String note, @Nullable Instant created, @Nullable Author author) {
    @Builder(toBuilder = true)
    @Type
    public record Author(@Nullable String name, @Nullable URI uri) {}
}
