package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import java.net.URI;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

@Type
public record Note(
        String note, @Nullable Instant created, @Nullable Author author) {
    @Type
    public record Author(@Nullable String name, @Nullable URI uri) {}
}
