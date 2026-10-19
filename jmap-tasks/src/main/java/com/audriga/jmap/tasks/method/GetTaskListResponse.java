package com.audriga.jmap.tasks.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;
import com.audriga.jmap.tasks.entity.TaskList;
import java.util.List;

@JmapMethod("TaskList/get")
@RecordBuilder
public record GetTaskListResponse(String accountId, String state, List<TaskList> list, List<String> notFound)
        implements GetMethodResponse<TaskList> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetTaskListResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
