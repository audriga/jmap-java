package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import java.net.URI;
import org.jspecify.annotations.Nullable;

@lombok.Builder(toBuilder = true)
@Type
public record OnlineService(
        @Nullable String service,
        @Nullable URI uri,
        @Nullable String user,
        @Nullable Contexts contexts,
        @Nullable Integer pref,
        @Nullable String label) {}
