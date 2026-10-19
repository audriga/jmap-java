package com.audriga.jmap.tasks.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.call.standard.SetMethodCall;
import com.audriga.jmap.tasks.entity.Task;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("Task/set")
@RecordBuilder
public record SetTaskCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<String> ifInState,
        @Nullable Arg<Map<String, Task>> create,
        @Nullable Arg<Map<String, Map<String, Object>>> update,
        @Nullable Arg<List<String>> destroy)
        implements SetMethodCall<Task> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends SetTaskCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
