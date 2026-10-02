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
        @Nullable Boolean isTopLevel,
        @Nullable String parentId,
        @Nullable String ancestorId,
        @Nullable String descendantId,
        @Nullable String nodeType,
        @Nullable String role,
        @Nullable Boolean hasAnyRole,
        @Nullable String blobId,
        @Nullable Boolean isExecutable,
        @Nullable Instant createdBefore,
        @Nullable Instant createdAfter,
        @Nullable Instant modifiedBefore,
        @Nullable Instant modifiedAfter,
        @Nullable Instant accessedBefore,
        @Nullable Instant accessedAfter,
        @Nullable Long minSize,
        @Nullable Long maxSize,
        @Nullable String name,
        @Nullable String nameMatch,
        @Nullable String type,
        @Nullable String typeMatch,
        @Nullable String text,
        @Nullable String body)
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
                isTopLevel,
                parentId,
                ancestorId,
                descendantId,
                nodeType,
                role,
                hasAnyRole,
                blobId,
                isExecutable,
                createdBefore,
                createdAfter,
                modifiedBefore,
                modifiedAfter,
                accessedBefore,
                accessedAfter,
                minSize,
                maxSize,
                name,
                nameMatch,
                type,
                typeMatch,
                text,
                body);
    }
}
