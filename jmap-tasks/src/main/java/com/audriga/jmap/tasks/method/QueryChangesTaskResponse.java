package com.audriga.jmap.tasks.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.AddedItem;
import com.audriga.jmap.common.method.response.standard.QueryChangesMethodResponse;
import com.audriga.jmap.tasks.entity.Task;
import java.util.List;

@JmapMethod("Task/queryChanges")
@RecordBuilder
public record QueryChangesTaskResponse(
        String accountId,
        String oldQueryState,
        String newQueryState,
        long total,
        List<String> removed,
        List<AddedItem<String>> added)
        implements QueryChangesMethodResponse<Task> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryChangesTaskResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
