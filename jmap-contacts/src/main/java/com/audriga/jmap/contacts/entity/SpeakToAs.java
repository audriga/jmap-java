package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import java.util.Map;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder(toBuilder = true)
@Type
public record SpeakToAs(
        @Nullable String grammaticalGender, @Nullable Map<String, Pronouns> pronouns) {}
