package rs.ltt.jmap.contacts.method;

import java.util.Map;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.common.entity.SetError;
import rs.ltt.jmap.common.method.response.standard.SetMethodResponse;
import rs.ltt.jmap.contacts.entity.AddressBook;

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
