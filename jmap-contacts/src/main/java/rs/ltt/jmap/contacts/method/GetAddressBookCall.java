package rs.ltt.jmap.contacts.method;

import lombok.NonNull;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.common.Request;
import rs.ltt.jmap.common.method.call.standard.GetMethodCall;
import rs.ltt.jmap.contacts.entity.AddressBook;

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
