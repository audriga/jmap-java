package com.audriga.jmap.notes.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;
import com.audriga.jmap.notes.entity.Notebook;
import java.util.List;

@JmapMethod("Notebook/get")
@RecordBuilder
public record GetNotebookResponse(String accountId, String state, List<Notebook> list, List<String> notFound)
        implements GetMethodResponse<Notebook> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetNotebookResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
