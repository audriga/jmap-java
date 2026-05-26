package rs.ltt.jmap.contacts.method;

import lombok.NonNull;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.common.method.call.standard.ChangesMethodCall;
import rs.ltt.jmap.contacts.entity.AddressBook;

@JmapMethod("AddressBook/changes")
public class ChangesAddressBookCall extends ChangesMethodCall<AddressBook> {
    public ChangesAddressBookCall(@NonNull String accountId, @NonNull String sinceState, Long maxChanges) {
        super(accountId, sinceState, maxChanges);
    }
}
