package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder(toBuilder = true)
@Type
public record OrgUnit(String name, @Nullable String sortAs) {}
