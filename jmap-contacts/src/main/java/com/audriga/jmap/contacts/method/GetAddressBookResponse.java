package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;
import com.audriga.jmap.contacts.entity.AddressBook;

@JmapMethod("AddressBook/get")
public class GetAddressBookResponse extends GetMethodResponse<AddressBook> {
    public GetAddressBookResponse(String accountId, String state, String[] notFound, AddressBook[] list) {
        super(accountId, state, notFound, list);
    }
}
