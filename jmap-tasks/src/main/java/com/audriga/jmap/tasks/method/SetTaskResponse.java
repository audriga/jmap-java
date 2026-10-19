package com.audriga.jmap.tasks.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import com.audriga.jmap.tasks.entity.Task;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@JmapMethod("Task/set")
@RecordBuilder
public record SetTaskResponse(
        String accountId,
        @Nullable String oldState,
        String newState,
        @Nullable Map<String, Task> created,
        @Nullable Map<String, Task> updated,
        @Nullable List<String> destroyed,
        @Nullable Map<String, SetError> notCreated,
        @Nullable Map<String, SetError> notUpdated,
        @Nullable Map<String, SetError> notDestroyed)
        implements SetMethodResponse<Task> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends SetTaskResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
