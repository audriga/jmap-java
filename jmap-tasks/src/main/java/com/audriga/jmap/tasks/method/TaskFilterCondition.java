package com.audriga.jmap.tasks.method;

import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.entity.filter.FilterCondition;
import com.audriga.jmap.common.util.QueryStringUtils;
import com.audriga.jmap.tasks.entity.Task;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

@lombok.Builder(toBuilder = true)
public record TaskFilterCondition(
        @Nullable String taskListId,
        @Nullable String title,
        @Nullable String titleMatch,
        @Nullable String status,
        @Nullable String progress,
        @Nullable Instant createdBefore,
        @Nullable Instant createdAfter,
        @Nullable Instant updatedBefore,
        @Nullable Instant updatedAfter,
        @Nullable String text,
        @Nullable String description)
        implements FilterCondition<Task> {
    @Override
    public int compareTo(Filter<Task> o) {
        return toQueryString().compareTo(o.toQueryString());
    }

    @Override
    public String toQueryString() {
        return QueryStringUtils.toQueryString(
                L3_DIVIDER,
                L4_DIVIDER,
                taskListId,
                title,
                titleMatch,
                status,
                progress,
                createdBefore,
                createdAfter,
                updatedBefore,
                updatedAfter,
                text,
                description);
    }
}
