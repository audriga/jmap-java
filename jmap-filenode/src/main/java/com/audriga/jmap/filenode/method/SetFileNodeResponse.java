package com.audriga.jmap.filenode.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import com.audriga.jmap.filenode.entity.FileNode;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

// See https://datatracker.ietf.org/doc/draft-ietf-jmap-filenode/
@JmapMethod("FileNode/set")
@RecordBuilder
public record SetFileNodeResponse(
        String accountId,
        @Nullable String oldState,
        String newState,
        @Nullable Map<String, FileNode> created,
        @Nullable Map<String, FileNode> updated,
        @Nullable List<String> destroyed,
        @Nullable Map<String, SetError> notCreated,
        @Nullable Map<String, SetError> notUpdated,
        @Nullable Map<String, SetError> notDestroyed)
        implements SetMethodResponse<FileNode> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends SetFileNodeResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
