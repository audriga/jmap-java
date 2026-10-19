package com.audriga.jmap.tasks.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.QueryMethodResponse;
import com.audriga.jmap.tasks.entity.Task;
import java.util.List;
import org.jspecify.annotations.Nullable;

@JmapMethod("Task/query")
@RecordBuilder
public record QueryTaskResponse(
        String accountId,
        String queryState,
        boolean canCalculateChanges,
        Long position,
        List<String> ids,
        @Nullable Long total,
        @Nullable Long limit)
        implements QueryMethodResponse<Task> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryTaskResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
