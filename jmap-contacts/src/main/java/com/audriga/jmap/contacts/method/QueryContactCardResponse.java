package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.QueryMethodResponse;
import com.audriga.jmap.contacts.entity.ContactCard;
import java.util.List;
import org.jspecify.annotations.Nullable;

@JmapMethod("ContactCard/query")
@RecordBuilder
public record QueryContactCardResponse(
        String accountId,
        String queryState,
        boolean canCalculateChanges,
        Long position,
        List<String> ids,
        @Nullable Long total,
        @Nullable Long limit)
        implements QueryMethodResponse<ContactCard> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryContactCardResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
