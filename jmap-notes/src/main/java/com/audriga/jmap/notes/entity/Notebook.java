package com.audriga.jmap.notes.entity;

import com.audriga.jmap.annotation.Immutable;
import com.audriga.jmap.annotation.JmapEntity;
import com.audriga.jmap.annotation.ServerSet;
import com.audriga.jmap.annotation.Type;
import com.audriga.jmap.calendars.entity.CalendarRights;
import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.notes.method.NotebookFilterCondition;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@JmapEntity(filterCondition = NotebookFilterCondition.class)
@lombok.Builder(toBuilder = true)
@Type
public record Notebook(
        @Immutable @ServerSet String id,
        @Nullable String name,
        @Nullable Boolean isSubscribed,
        @Nullable Boolean isVisible,
        @Nullable NotebookRole role,
        @Nullable String description,
        @Nullable Map<String, CalendarRights> shareWith)
        implements Identifiable {}
