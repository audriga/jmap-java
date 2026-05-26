package rs.ltt.jmap.contacts.method;

import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.common.method.response.standard.ChangesMethodResponse;
import rs.ltt.jmap.contacts.entity.ContactCard;

@JmapMethod("ContactCard/changes")
public class ChangesContactCardResponse extends ChangesMethodResponse<ContactCard> {
    public ChangesContactCardResponse(
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
