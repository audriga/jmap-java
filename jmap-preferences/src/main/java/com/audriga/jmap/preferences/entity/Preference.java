package com.audriga.jmap.preferences.entity;

import com.audriga.jmap.annotation.Immutable;
import com.audriga.jmap.annotation.ServerSet;
import com.audriga.jmap.common.entity.Identifiable;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@lombok.Builder(toBuilder = true)
public record Preference(
        @Immutable @ServerSet String id, @Nullable Map<String, Forward> forwards) implements Identifiable {}
