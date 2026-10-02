package com.audriga.jmap.filenode.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.call.standard.ChangesMethodCall;
import com.audriga.jmap.filenode.entity.FileNode;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

// See https://datatracker.ietf.org/doc/draft-ietf-jmap-filenode/
@JmapMethod("FileNode/changes")
@RecordBuilder
public record ChangesFileNodeCall(
        @NonNull Arg<String> accountId,
        @NonNull Arg<String> sinceState,
        @Nullable Arg<Long> maxChanges) implements ChangesMethodCall<FileNode> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends ChangesFileNodeCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
