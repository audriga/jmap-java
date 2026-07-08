package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import com.audriga.jmap.contacts.entity.AddressBook;
import java.util.Map;

@JmapMethod("AddressBook/set")
public class SetAddressBookResponse extends SetMethodResponse<AddressBook> {
    public SetAddressBookResponse(
            String accountId,
            String oldState,
            String newState,
            Map<String, AddressBook> created,
            Map<String, AddressBook> updated,
            String[] destroyed,
            Map<String, SetError> notCreated,
            Map<String, SetError> notUpdated,
            Map<String, SetError> notDestroyed) {
        super(accountId, oldState, newState, created, updated, destroyed, notCreated, notUpdated, notDestroyed);
    }
}
