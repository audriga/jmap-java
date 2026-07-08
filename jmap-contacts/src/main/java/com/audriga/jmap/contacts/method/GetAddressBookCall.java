package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.Request;
import com.audriga.jmap.common.method.call.standard.GetMethodCall;
import com.audriga.jmap.contacts.entity.AddressBook;
import lombok.NonNull;

@JmapMethod("AddressBook/get")
public class GetAddressBookCall extends GetMethodCall<AddressBook> {
    public GetAddressBookCall(
            @NonNull String accountId,
            String[] ids,
            String[] properties,
            Request.Invocation.ResultReference idsReference) {
        super(accountId, ids, properties, idsReference);
    }
}
