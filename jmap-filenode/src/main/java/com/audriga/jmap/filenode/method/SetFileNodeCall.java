package com.audriga.jmap.filenode.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.call.standard.SetMethodCall;
import com.audriga.jmap.filenode.entity.FileNode;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

// See https://datatracker.ietf.org/doc/draft-ietf-jmap-filenode/
@JmapMethod("FileNode/set")
@RecordBuilder
public record SetFileNodeCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<String> ifInState,
        @Nullable Arg<Map<String, FileNode>> create,
        @Nullable Arg<Map<String, Map<String, Object>>> update,
        @Nullable Arg<List<String>> destroy)
        implements SetMethodCall<FileNode> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends SetFileNodeCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
