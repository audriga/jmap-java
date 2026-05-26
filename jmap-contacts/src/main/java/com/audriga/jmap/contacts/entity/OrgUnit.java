package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import org.jspecify.annotations.Nullable;

@lombok.Builder(toBuilder = true)
@Type
public record OrgUnit(String name, @Nullable String sortAs) {}
