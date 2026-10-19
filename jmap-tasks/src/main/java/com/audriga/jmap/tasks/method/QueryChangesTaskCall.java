package com.audriga.jmap.tasks.method;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.Comparator;
import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.method.call.standard.QueryChangesMethodCall;
import com.audriga.jmap.tasks.entity.Task;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("Task/queryChanges")
@RecordBuilder
public record QueryChangesTaskCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<Filter<Task>> filter,
        @Nullable Arg<List<Comparator>> sort,
        @NonNull Arg<String> sinceQueryState,
        @Nullable Arg<Long> maxChanges,
        @Nullable Arg<String> upToId,
        @Default("false") @Nullable Arg<Boolean> calculateTotal)
        implements QueryChangesMethodCall<Task> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryChangesTaskCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
