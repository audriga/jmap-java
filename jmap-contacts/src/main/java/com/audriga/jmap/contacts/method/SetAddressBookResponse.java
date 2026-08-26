package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import com.audriga.jmap.contacts.entity.AddressBook;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@JmapMethod("AddressBook/set")
@RecordBuilder
public record SetAddressBookResponse(
        String accountId,
        @Nullable String oldState,
        String newState,
        @Nullable Map<String, AddressBook> created,
        @Nullable Map<String, AddressBook> updated,
        @Nullable List<String> destroyed,
        @Nullable Map<String, SetError> notCreated,
        @Nullable Map<String, SetError> notUpdated,
        @Nullable Map<String, SetError> notDestroyed)
        implements SetMethodResponse<AddressBook> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends SetAddressBookResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
