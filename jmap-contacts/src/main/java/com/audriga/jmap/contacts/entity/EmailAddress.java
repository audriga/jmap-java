package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder(toBuilder = true)
@Type
public record EmailAddress(
        String address,
        @Nullable Contexts contexts,
        @Nullable Integer pref,
        @Nullable String label) {}
