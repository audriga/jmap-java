package rs.ltt.jmap.contacts.method;

import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.common.method.response.standard.GetMethodResponse;
import rs.ltt.jmap.contacts.entity.AddressBook;

@JmapMethod("AddressBook/get")
public class GetAddressBookResponse extends GetMethodResponse<AddressBook> {
    public GetAddressBookResponse(String accountId, String state, String[] notFound, AddressBook[] list) {
        super(accountId, state, notFound, list);
    }
}
