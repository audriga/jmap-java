package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import java.net.URI;
import org.jspecify.annotations.Nullable;

@Type
public record SchedulingAddress(
        URI uri,
        @Nullable Contexts contexts,
        @Nullable Integer pref,
        @Nullable String label) {}
