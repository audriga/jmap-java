package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Type;
import java.net.URI;
import java.time.ZoneId;
import java.util.List;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder(toBuilder = true)
@Type
public record Address(
        @Nullable List<AddressComponent> components,
        @Default("false") boolean isOrdered,
        @Nullable String countryCode,
        @Nullable URI coordinates,
        @Nullable ZoneId timeZone,
        @Nullable Contexts contexts,
        @Nullable String full,
        @Nullable String defaultSeparator,
        @Nullable Integer pref,
        @Nullable String phoneticScript,
        @Nullable String phoneticSystem) {}
