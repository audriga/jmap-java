package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import org.jspecify.annotations.Nullable;

@Type
public record EmailAddress(
        String address,
        @Nullable Contexts contexts,
        @Nullable Integer pref,
        @Nullable String label) {}
