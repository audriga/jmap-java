package com.audriga.jmap.stalwartgenerator.model;

import org.jspecify.annotations.Nullable;

public record GenEnumVariant(
        String schemaName,
        String javaName,
        String label,
        @Nullable String explanation) {}
