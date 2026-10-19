package com.audriga.jmap.tasks.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.AddedItem;
import com.audriga.jmap.common.method.response.standard.QueryChangesMethodResponse;
import com.audriga.jmap.tasks.entity.TaskList;
import java.util.List;

@JmapMethod("TaskList/queryChanges")
@RecordBuilder
public record QueryChangesTaskListResponse(
        String accountId,
        String oldQueryState,
        String newQueryState,
        long total,
        List<String> removed,
        List<AddedItem<String>> added)
        implements QueryChangesMethodResponse<TaskList> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryChangesTaskListResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
