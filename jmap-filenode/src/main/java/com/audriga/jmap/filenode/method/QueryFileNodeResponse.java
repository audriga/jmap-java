package com.audriga.jmap.filenode.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.QueryMethodResponse;
import com.audriga.jmap.filenode.entity.FileNode;
import java.util.List;
import org.jspecify.annotations.Nullable;

// See https://datatracker.ietf.org/doc/draft-ietf-jmap-filenode/
@JmapMethod("FileNode/query")
@RecordBuilder
public record QueryFileNodeResponse(
        String accountId,
        String queryState,
        boolean canCalculateChanges,
        Long position,
        List<String> ids,
        @Nullable Long total,
        @Nullable Long limit)
        implements QueryMethodResponse<FileNode> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryFileNodeResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
