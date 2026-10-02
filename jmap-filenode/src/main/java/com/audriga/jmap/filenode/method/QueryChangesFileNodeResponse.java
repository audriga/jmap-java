package com.audriga.jmap.filenode.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.AddedItem;
import com.audriga.jmap.common.method.response.standard.QueryChangesMethodResponse;
import com.audriga.jmap.filenode.entity.FileNode;
import java.util.List;

// See https://datatracker.ietf.org/doc/draft-ietf-jmap-filenode/
@JmapMethod("FileNode/queryChanges")
@RecordBuilder
public record QueryChangesFileNodeResponse(
        String accountId,
        String oldQueryState,
        String newQueryState,
        long total,
        List<String> removed,
        List<AddedItem<String>> added)
        implements QueryChangesMethodResponse<FileNode> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryChangesFileNodeResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
