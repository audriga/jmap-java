package com.audriga.jmap.filenode.method;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.Comparator;
import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.method.call.standard.QueryChangesMethodCall;
import com.audriga.jmap.filenode.entity.FileNode;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

// See https://datatracker.ietf.org/doc/draft-ietf-jmap-filenode/
@JmapMethod("FileNode/queryChanges")
@RecordBuilder
public record QueryChangesFileNodeCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<Filter<FileNode>> filter,
        @Nullable Arg<List<Comparator>> sort,
        @NonNull Arg<String> sinceQueryState,
        @Nullable Arg<Long> maxChanges,
        @Nullable Arg<String> upToId,
        @Default("false") @Nullable Arg<Boolean> calculateTotal)
        implements QueryChangesMethodCall<FileNode> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryChangesFileNodeCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
