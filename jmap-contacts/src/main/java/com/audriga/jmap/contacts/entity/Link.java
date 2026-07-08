package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import java.net.URI;
import org.jspecify.annotations.Nullable;

@Type
public record Link(
        @Nullable String kind,
        URI uri,
        @Nullable String mediaType,
        @Nullable Contexts contexts,
        @Nullable Integer pref,
        @Nullable String label)
        implements Resource {}
