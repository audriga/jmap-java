package com.audriga.jmap.sieve.entity;

import com.audriga.jmap.annotation.Immutable;
import com.audriga.jmap.annotation.ServerSet;
import com.audriga.jmap.common.entity.Identifiable;
import org.jspecify.annotations.Nullable;

@lombok.Builder(toBuilder = true)
public record SieveScript(
        @Immutable @ServerSet String id,
        String name,
        @Nullable @ServerSet String blobId,
        boolean isActive) implements Identifiable {}
