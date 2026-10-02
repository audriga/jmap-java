package com.audriga.jmap.filenode.method;

import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.entity.filter.FilterCondition;
import com.audriga.jmap.common.util.QueryStringUtils;
import com.audriga.jmap.filenode.entity.FileNode;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

// See https://datatracker.ietf.org/doc/draft-ietf-jmap-filenode/
@lombok.Builder(toBuilder = true)
public record FileNodeFilterCondition(
        @Nullable String parentId,
        @Nullable String nodeType,
        @Nullable String name,
        @Nullable String type,
        @Nullable Instant createdBefore,
        @Nullable Instant createdAfter,
        @Nullable Instant modifiedBefore,
        @Nullable Instant modifiedAfter,
        @Nullable Boolean isSubscribed,
        @Nullable String text)
        implements FilterCondition<FileNode> {
    @Override
    public int compareTo(Filter<FileNode> o) {
        return toQueryString().compareTo(o.toQueryString());
    }

    @Override
    public String toQueryString() {
        return QueryStringUtils.toQueryString(
                L3_DIVIDER,
                L4_DIVIDER,
                parentId,
                nodeType,
                name,
                type,
                createdBefore,
                createdAfter,
                modifiedBefore,
                modifiedAfter,
                isSubscribed,
                text);
    }
}
