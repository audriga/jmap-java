package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import org.jspecify.annotations.Nullable;

@Type
public record OrgUnit(String name, @Nullable String sortAs) {}
