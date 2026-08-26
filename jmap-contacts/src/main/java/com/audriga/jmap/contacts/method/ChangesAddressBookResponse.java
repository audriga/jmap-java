package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.ChangesMethodResponse;
import com.audriga.jmap.contacts.entity.AddressBook;
import java.util.List;
import org.jspecify.annotations.NonNull;

@JmapMethod("AddressBook/changes")
@RecordBuilder
public record ChangesAddressBookResponse(
        @NonNull String accountId,
        @NonNull String oldState,
        @NonNull String newState,
        boolean hasMoreChanges,
        @NonNull List<String> created,
        @NonNull List<String> updated,
        @NonNull List<String> destroyed)
        implements ChangesMethodResponse<AddressBook> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends ChangesAddressBookResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
