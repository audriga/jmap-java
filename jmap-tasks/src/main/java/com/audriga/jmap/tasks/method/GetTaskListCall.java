package com.audriga.jmap.tasks.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.call.standard.GetMethodCall;
import com.audriga.jmap.tasks.entity.TaskList;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("TaskList/get")
@RecordBuilder
public record GetTaskListCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<List<String>> ids,
        @Nullable Arg<List<String>> properties) implements GetMethodCall<TaskList> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetTaskListCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
