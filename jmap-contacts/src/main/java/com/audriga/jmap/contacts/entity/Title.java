package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Type;
import org.jspecify.annotations.Nullable;

@lombok.Builder(toBuilder = true)
@Type
public record Title(
        String name,
        @Default("\"title\"") String kind,
        @Nullable String organizationId) {}
