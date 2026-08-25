package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.method.ResultReference;
import com.audriga.jmap.common.method.call.standard.SetMethodCall;
import com.audriga.jmap.contacts.entity.AddressBook;
import java.util.Map;
import lombok.NonNull;

@JmapMethod("AddressBook/set")
public class SetAddressBookCall extends SetMethodCall<AddressBook> {
    @lombok.Builder
    public SetAddressBookCall(
            @NonNull String accountId,
            String ifInState,
            Map<String, AddressBook> create,
            Map<String, Map<String, Object>> update,
            String[] destroy,
            ResultReference destroyReference) {
        super(accountId, ifInState, create, update, destroy, destroyReference);
    }
}
