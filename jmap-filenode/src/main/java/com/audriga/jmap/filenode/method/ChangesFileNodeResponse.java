package com.audriga.jmap.filenode.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.ChangesMethodResponse;
import com.audriga.jmap.filenode.entity.FileNode;
import java.util.List;
import org.jspecify.annotations.NonNull;

// See https://datatracker.ietf.org/doc/draft-ietf-jmap-filenode/
@JmapMethod("FileNode/changes")
@RecordBuilder
public record ChangesFileNodeResponse(
        @NonNull String accountId,
        @NonNull String oldState,
        @NonNull String newState,
        boolean hasMoreChanges,
        @NonNull List<String> created,
        @NonNull List<String> updated,
        @NonNull List<String> destroyed)
        implements ChangesMethodResponse<FileNode> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends ChangesFileNodeResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
