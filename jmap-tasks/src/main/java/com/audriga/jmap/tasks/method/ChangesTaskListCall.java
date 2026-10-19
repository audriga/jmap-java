package com.audriga.jmap.tasks.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.call.standard.ChangesMethodCall;
import com.audriga.jmap.tasks.entity.TaskList;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("TaskList/changes")
@RecordBuilder
public record ChangesTaskListCall(
        @NonNull Arg<String> accountId,
        @NonNull Arg<String> sinceState,
        @Nullable Arg<Long> maxChanges) implements ChangesMethodCall<TaskList> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends ChangesTaskListCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
