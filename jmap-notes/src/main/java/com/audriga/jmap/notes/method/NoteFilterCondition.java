package com.audriga.jmap.notes.method;

import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.entity.filter.FilterCondition;
import com.audriga.jmap.common.util.QueryStringUtils;
import com.audriga.jmap.notes.entity.Note;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

@lombok.Builder(toBuilder = true)
public record NoteFilterCondition(
        @Nullable String notebookId,
        @Nullable String name,
        @Nullable String nameMatch,
        @Nullable String color,
        @Nullable Instant createdBefore,
        @Nullable Instant createdAfter,
        @Nullable Instant updatedBefore,
        @Nullable Instant updatedAfter,
        @Nullable String text,
        @Nullable String body)
        implements FilterCondition<Note> {
    @Override
    public int compareTo(Filter<Note> o) {
        return toQueryString().compareTo(o.toQueryString());
    }

    @Override
    public String toQueryString() {
        return QueryStringUtils.toQueryString(
                L3_DIVIDER,
                L4_DIVIDER,
                notebookId,
                name,
                nameMatch,
                color,
                createdBefore,
                createdAfter,
                updatedBefore,
                updatedAfter,
                text,
                body);
    }
}
