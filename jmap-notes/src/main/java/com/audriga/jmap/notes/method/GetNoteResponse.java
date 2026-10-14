package com.audriga.jmap.notes.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;
import com.audriga.jmap.notes.entity.Note;
import java.util.List;

@JmapMethod("Note/get")
@RecordBuilder
public record GetNoteResponse(String accountId, String state, List<Note> list, List<String> notFound)
        implements GetMethodResponse<Note> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetNoteResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
