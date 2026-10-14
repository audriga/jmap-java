package com.audriga.jmap.notes.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.QueryMethodResponse;
import com.audriga.jmap.notes.entity.Note;
import java.util.List;
import org.jspecify.annotations.Nullable;

@JmapMethod("Note/query")
@RecordBuilder
public record QueryNoteResponse(
        String accountId,
        String queryState,
        boolean canCalculateChanges,
        Long position,
        List<String> ids,
        @Nullable Long total,
        @Nullable Long limit)
        implements QueryMethodResponse<Note> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryNoteResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
