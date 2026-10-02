package com.audriga.jmap.filenode.method;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.call.standard.CopyMethodCall;
import com.audriga.jmap.filenode.entity.FileNode;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

// See https://datatracker.ietf.org/doc/draft-ietf-jmap-filenode/
@JmapMethod("FileNode/copy")
@RecordBuilder
public record CopyFileNodeCall(
        @NonNull Arg<String> fromAccountId,
        @Nullable Arg<String> ifFromInState,
        @NonNull Arg<String> accountId,
        @Nullable Arg<String> ifInState,
        @NonNull Arg<Map<String, FileNode>> create,
        @Default("false") @Nullable Arg<Boolean> onSuccessDestroyOriginal,
        @Nullable Arg<String> destroyFromIfInState)
        implements CopyMethodCall<FileNode> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends CopyFileNodeCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
