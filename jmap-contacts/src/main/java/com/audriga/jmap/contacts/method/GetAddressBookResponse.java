package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;
import com.audriga.jmap.contacts.entity.AddressBook;
import java.util.List;

@JmapMethod("AddressBook/get")
@RecordBuilder
public record GetAddressBookResponse(String accountId, String state, List<AddressBook> list, List<String> notFound)
        implements GetMethodResponse<AddressBook> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetAddressBookResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
