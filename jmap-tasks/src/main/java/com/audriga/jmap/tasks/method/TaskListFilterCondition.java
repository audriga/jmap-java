package com.audriga.jmap.tasks.method;

import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.entity.filter.FilterCondition;
import com.audriga.jmap.common.util.QueryStringUtils;
import com.audriga.jmap.tasks.entity.TaskList;
import org.jspecify.annotations.Nullable;

@lombok.Builder(toBuilder = true)
public record TaskListFilterCondition(
        @Nullable String name,
        @Nullable String nameMatch,
        @Nullable Boolean isVisible,
        @Nullable Boolean isSubscribed) implements FilterCondition<TaskList> {
    @Override
    public int compareTo(Filter<TaskList> o) {
        return toQueryString().compareTo(o.toQueryString());
    }

    @Override
    public String toQueryString() {
        return QueryStringUtils.toQueryString(L3_DIVIDER, L4_DIVIDER, name, nameMatch, isVisible, isSubscribed);
    }
}
