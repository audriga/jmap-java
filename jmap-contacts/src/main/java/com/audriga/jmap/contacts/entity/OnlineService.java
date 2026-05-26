package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import java.net.URI;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder(toBuilder = true)
@Type
public record OnlineService(
        @Nullable String service,
        @Nullable URI uri,
        @Nullable String user,
        @Nullable Contexts contexts,
        @Nullable Integer pref,
        @Nullable String label) {}
