package com.audriga.jmap.notes.method;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.Comparator;
import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.method.call.standard.QueryChangesMethodCall;
import com.audriga.jmap.notes.entity.Notebook;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("Notebook/queryChanges")
@RecordBuilder
public record QueryChangesNotebookCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<Filter<Notebook>> filter,
        @Nullable Arg<List<Comparator>> sort,
        @NonNull Arg<String> sinceQueryState,
        @Nullable Arg<Long> maxChanges,
        @Nullable Arg<String> upToId,
        @Default("false") @Nullable Arg<Boolean> calculateTotal)
        implements QueryChangesMethodCall<Notebook> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryChangesNotebookCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
