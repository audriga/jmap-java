package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import org.jspecify.annotations.Nullable;

@Type
public record PersonalInfo(
        String kind,
        String value,
        @Nullable String level,
        @Nullable Integer listAs) {}
