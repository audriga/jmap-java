package com.audriga.jmap.tasks.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.call.standard.GetMethodCall;
import com.audriga.jmap.tasks.entity.Task;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("Task/get")
@RecordBuilder
public record GetTaskCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<List<String>> ids,
        @Nullable Arg<List<String>> properties) implements GetMethodCall<Task> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetTaskCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
