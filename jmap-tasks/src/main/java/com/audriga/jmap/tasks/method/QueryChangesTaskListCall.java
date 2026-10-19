package com.audriga.jmap.tasks.method;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.Comparator;
import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.method.call.standard.QueryChangesMethodCall;
import com.audriga.jmap.tasks.entity.TaskList;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("TaskList/queryChanges")
@RecordBuilder
public record QueryChangesTaskListCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<Filter<TaskList>> filter,
        @Nullable Arg<List<Comparator>> sort,
        @NonNull Arg<String> sinceQueryState,
        @Nullable Arg<Long> maxChanges,
        @Nullable Arg<String> upToId,
        @Default("false") @Nullable Arg<Boolean> calculateTotal)
        implements QueryChangesMethodCall<TaskList> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryChangesTaskListCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
