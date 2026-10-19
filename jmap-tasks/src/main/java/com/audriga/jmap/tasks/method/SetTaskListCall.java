package com.audriga.jmap.tasks.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.call.standard.SetMethodCall;
import com.audriga.jmap.tasks.entity.TaskList;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("TaskList/set")
@RecordBuilder
public record SetTaskListCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<String> ifInState,
        @Nullable Arg<Map<String, TaskList>> create,
        @Nullable Arg<Map<String, Map<String, Object>>> update,
        @Nullable Arg<List<String>> destroy)
        implements SetMethodCall<TaskList> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends SetTaskListCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
