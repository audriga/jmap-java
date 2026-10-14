package com.audriga.jmap.notes.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.AddedItem;
import com.audriga.jmap.common.method.response.standard.QueryChangesMethodResponse;
import com.audriga.jmap.notes.entity.Note;
import java.util.List;

@JmapMethod("Note/queryChanges")
@RecordBuilder
public record QueryChangesNoteResponse(
        String accountId,
        String oldQueryState,
        String newQueryState,
        long total,
        List<String> removed,
        List<AddedItem<String>> added)
        implements QueryChangesMethodResponse<Note> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryChangesNoteResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
