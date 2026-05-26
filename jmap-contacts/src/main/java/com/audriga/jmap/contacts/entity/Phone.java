package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import java.util.Set;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder(toBuilder = true)
@Type
public record Phone(
        String number,
        @Nullable Set<String> features,
        @Nullable Contexts contexts,
        @Nullable Integer pref,
        @Nullable String label) {}
