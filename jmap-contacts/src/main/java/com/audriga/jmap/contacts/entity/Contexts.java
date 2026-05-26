package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Inline;
import java.util.Set;
import org.jspecify.annotations.Nullable;

@Inline
public record Contexts(Set<String> values) {
    public static @Nullable Contexts ofNullable(@Nullable String value) {
        return value == null ? null : new Contexts(Set.of(value));
    }

    public static Contexts of(String... values) {
        return new Contexts(Set.of(values));
    }
}
