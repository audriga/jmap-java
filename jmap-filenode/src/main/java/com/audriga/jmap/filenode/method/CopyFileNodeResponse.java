package com.audriga.jmap.filenode.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.response.standard.CopyMethodResponse;
import com.audriga.jmap.filenode.entity.FileNode;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

// See https://datatracker.ietf.org/doc/draft-ietf-jmap-filenode/
@JmapMethod("FileNode/copy")
@RecordBuilder
public record CopyFileNodeResponse(
        @NonNull String fromAccountId,
        @NonNull String accountId,
        @Nullable String oldState,
        @NonNull String newState,
        @Nullable Map<String, FileNode> created,
        @Nullable Map<String, SetError> notCreated)
        implements CopyMethodResponse<FileNode> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends CopyFileNodeResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
