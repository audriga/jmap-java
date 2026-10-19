package com.audriga.jmap.tasks.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.ChangesMethodResponse;
import com.audriga.jmap.tasks.entity.Task;
import java.util.List;
import org.jspecify.annotations.NonNull;

@JmapMethod("Task/changes")
@RecordBuilder
public record ChangesTaskResponse(
        @NonNull String accountId,
        @NonNull String oldState,
        @NonNull String newState,
        boolean hasMoreChanges,
        @NonNull List<String> created,
        @NonNull List<String> updated,
        @NonNull List<String> destroyed)
        implements ChangesMethodResponse<Task> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends ChangesTaskResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
