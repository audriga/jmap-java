package com.audriga.jmap.tasks.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;
import com.audriga.jmap.tasks.entity.Task;
import java.util.List;

@JmapMethod("Task/get")
@RecordBuilder
public record GetTaskResponse(String accountId, String state, List<Task> list, List<String> notFound)
        implements GetMethodResponse<Task> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetTaskResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
