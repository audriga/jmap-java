package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import java.util.List;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder(toBuilder = true)
@Type
public record Organization(
        @Nullable String name,
        @Nullable List<OrgUnit> units,
        @Nullable String sortAs,
        @Nullable Contexts contexts) {}
