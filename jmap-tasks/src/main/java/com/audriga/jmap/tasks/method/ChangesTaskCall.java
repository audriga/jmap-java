package com.audriga.jmap.tasks.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.call.standard.ChangesMethodCall;
import com.audriga.jmap.tasks.entity.Task;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("Task/changes")
@RecordBuilder
public record ChangesTaskCall(
        @NonNull Arg<String> accountId,
        @NonNull Arg<String> sinceState,
        @Nullable Arg<Long> maxChanges) implements ChangesMethodCall<Task> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends ChangesTaskCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
