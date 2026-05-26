package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.method.call.standard.ChangesMethodCall;
import com.audriga.jmap.contacts.entity.AddressBook;
import lombok.NonNull;

@JmapMethod("AddressBook/changes")
public class ChangesAddressBookCall extends ChangesMethodCall<AddressBook> {
    @lombok.Builder
    public ChangesAddressBookCall(@NonNull String accountId, @NonNull String sinceState, Long maxChanges) {
        super(accountId, sinceState, maxChanges);
    }
}
