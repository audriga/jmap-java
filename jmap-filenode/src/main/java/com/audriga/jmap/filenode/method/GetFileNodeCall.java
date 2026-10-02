package com.audriga.jmap.filenode.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.call.standard.GetMethodCall;
import com.audriga.jmap.filenode.entity.FileNode;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

// See https://datatracker.ietf.org/doc/draft-ietf-jmap-filenode/
@JmapMethod("FileNode/get")
@RecordBuilder
public record GetFileNodeCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<List<String>> ids,
        @Nullable Arg<List<String>> properties) implements GetMethodCall<FileNode> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetFileNodeCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
