package rs.ltt.jmap.contacts.method;

import java.util.Map;
import lombok.NonNull;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.common.Request;
import rs.ltt.jmap.common.method.call.standard.SetMethodCall;
import rs.ltt.jmap.contacts.entity.AddressBook;

@JmapMethod("AddressBook/set")
public class SetAddressBookCall extends SetMethodCall<AddressBook> {
    public SetAddressBookCall(
            @NonNull String accountId,
            String ifInState,
            Map<String, AddressBook> create,
            Map<String, Map<String, Object>> update,
            String[] destroy,
            Request.Invocation.ResultReference destroyReference) {
        super(accountId, ifInState, create, update, destroy, destroyReference);
    }
}
