package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.response.standard.CopyMethodResponse;
import com.audriga.jmap.contacts.entity.ContactCard;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("ContactCard/copy")
@RecordBuilder
public record CopyContactCardResponse(
        @NonNull String fromAccountId,
        @NonNull String accountId,
        @Nullable String oldState,
        @NonNull String newState,
        @Nullable Map<String, ContactCard> created,
        @Nullable Map<String, SetError> notCreated)
        implements CopyMethodResponse<ContactCard> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends CopyContactCardResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
