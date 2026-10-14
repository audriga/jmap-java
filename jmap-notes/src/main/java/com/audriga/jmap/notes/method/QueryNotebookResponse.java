package com.audriga.jmap.notes.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.QueryMethodResponse;
import com.audriga.jmap.notes.entity.Notebook;
import java.util.List;
import org.jspecify.annotations.Nullable;

@JmapMethod("Notebook/query")
@RecordBuilder
public record QueryNotebookResponse(
        String accountId,
        String queryState,
        boolean canCalculateChanges,
        Long position,
        List<String> ids,
        @Nullable Long total,
        @Nullable Long limit)
        implements QueryMethodResponse<Notebook> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryNotebookResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
