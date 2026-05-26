package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Type;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@lombok.Builder(toBuilder = true)
@Type
public record Name(
        @Nullable List<NameComponent> components,
        @Default("false") boolean isOrdered,
        @Nullable String defaultSeparator,
        @Nullable String full,
        @Nullable Map<String, String> sortAs,
        @Nullable String phoneticScript,
        @Nullable String phoneticSystem) {}
