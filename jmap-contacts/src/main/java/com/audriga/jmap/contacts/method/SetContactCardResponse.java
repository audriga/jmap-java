package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import com.audriga.jmap.contacts.entity.ContactCard;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@JmapMethod("ContactCard/set")
@RecordBuilder
public record SetContactCardResponse(
        String accountId,
        @Nullable String oldState,
        String newState,
        @Nullable Map<String, ContactCard> created,
        @Nullable Map<String, ContactCard> updated,
        @Nullable List<String> destroyed,
        @Nullable Map<String, SetError> notCreated,
        @Nullable Map<String, SetError> notUpdated,
        @Nullable Map<String, SetError> notDestroyed)
        implements SetMethodResponse<ContactCard> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends SetContactCardResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
