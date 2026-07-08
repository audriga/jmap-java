package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.method.response.standard.ChangesMethodResponse;
import com.audriga.jmap.contacts.entity.AddressBook;

@JmapMethod("AddressBook/changes")
public class ChangesAddressBookResponse extends ChangesMethodResponse<AddressBook> {
    public ChangesAddressBookResponse(
            String accountId,
            String oldState,
            String newState,
            boolean hasMoreChanges,
            String[] created,
            String[] updated,
            String[] destroyed) {
        super(accountId, oldState, newState, hasMoreChanges, created, updated, destroyed);
    }
}
