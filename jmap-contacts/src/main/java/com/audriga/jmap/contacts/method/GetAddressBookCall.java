package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.method.ResultReference;
import com.audriga.jmap.common.method.call.standard.AbstractGetMethodCall;
import com.audriga.jmap.contacts.entity.AddressBook;
import lombok.NonNull;

@JmapMethod("AddressBook/get")
public class GetAddressBookCall extends AbstractGetMethodCall<AddressBook> {
    @lombok.Builder
    public GetAddressBookCall(
            @NonNull String accountId, String[] ids, String[] properties, ResultReference idsReference) {
        super(accountId, ids, properties, idsReference);
    }
}
