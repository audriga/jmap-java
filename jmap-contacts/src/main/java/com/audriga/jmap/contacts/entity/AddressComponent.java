package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import org.jspecify.annotations.Nullable;

@Type
public record AddressComponent(
        String value, String kind, @Nullable String phonetic) {}
