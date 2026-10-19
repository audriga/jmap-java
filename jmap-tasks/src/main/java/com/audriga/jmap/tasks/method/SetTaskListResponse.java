package com.audriga.jmap.tasks.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import com.audriga.jmap.tasks.entity.TaskList;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@JmapMethod("TaskList/set")
@RecordBuilder
public record SetTaskListResponse(
        String accountId,
        @Nullable String oldState,
        String newState,
        @Nullable Map<String, TaskList> created,
        @Nullable Map<String, TaskList> updated,
        @Nullable List<String> destroyed,
        @Nullable Map<String, SetError> notCreated,
        @Nullable Map<String, SetError> notUpdated,
        @Nullable Map<String, SetError> notDestroyed)
        implements SetMethodResponse<TaskList> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends SetTaskListResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
