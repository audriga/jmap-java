package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@lombok.Builder(toBuilder = true)
@Type
public record SpeakToAs(
        @Nullable String grammaticalGender, @Nullable Map<String, Pronouns> pronouns) {}
