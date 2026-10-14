package com.audriga.jmap.notes.method;

import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.entity.filter.FilterCondition;
import com.audriga.jmap.common.util.QueryStringUtils;
import com.audriga.jmap.notes.entity.Notebook;
import org.jspecify.annotations.Nullable;

@lombok.Builder(toBuilder = true)
public record NotebookFilterCondition(
        @Nullable String name,
        @Nullable String nameMatch,
        @Nullable String role,
        @Nullable Boolean isVisible,
        @Nullable Boolean isSubscribed)
        implements FilterCondition<Notebook> {
    @Override
    public int compareTo(Filter<Notebook> o) {
        return toQueryString().compareTo(o.toQueryString());
    }

    @Override
    public String toQueryString() {
        return QueryStringUtils.toQueryString(L3_DIVIDER, L4_DIVIDER, name, nameMatch, role, isVisible, isSubscribed);
    }
}
