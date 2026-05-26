package rs.ltt.jmap.contacts.method;

import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.common.method.call.standard.ChangesMethodCall;
import rs.ltt.jmap.contacts.entity.ContactCard;

@JmapMethod("ContactCard/changes")
public class ChangesContactCardCall extends ChangesMethodCall<ContactCard> {
    public ChangesContactCardCall(String accountId, String sinceState, @Nullable Long maxChanges) {
        super(accountId, sinceState, maxChanges);
    }
}
