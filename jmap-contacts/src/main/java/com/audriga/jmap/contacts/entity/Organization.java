package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import java.util.List;
import org.jspecify.annotations.Nullable;

@lombok.Builder(toBuilder = true)
@Type
public record Organization(
        @Nullable String name,
        @Nullable List<OrgUnit> units,
        @Nullable String sortAs,
        @Nullable Contexts contexts) {}
