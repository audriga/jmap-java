package com.audriga.jmap.tasks.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.QueryMethodResponse;
import com.audriga.jmap.tasks.entity.TaskList;
import java.util.List;
import org.jspecify.annotations.Nullable;

@JmapMethod("TaskList/query")
@RecordBuilder
public record QueryTaskListResponse(
        String accountId,
        String queryState,
        boolean canCalculateChanges,
        Long position,
        List<String> ids,
        @Nullable Long total,
        @Nullable Long limit)
        implements QueryMethodResponse<TaskList> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryTaskListResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
