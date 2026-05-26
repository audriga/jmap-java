package rs.ltt.jmap.contacts.method;

import java.util.Map;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.common.entity.SetError;
import rs.ltt.jmap.common.method.response.standard.SetMethodResponse;
import rs.ltt.jmap.contacts.entity.ContactCard;

@JmapMethod("ContactCard/set")
public class SetContactCardResponse extends SetMethodResponse<ContactCard> {
    public SetContactCardResponse(
            String accountId,
            String oldState,
            String newState,
            Map<String, ContactCard> created,
            Map<String, ContactCard> updated,
            String[] destroyed,
            Map<String, SetError> notCreated,
            Map<String, SetError> notUpdated,
            Map<String, SetError> notDestroyed) {
        super(accountId, oldState, newState, created, updated, destroyed, notCreated, notUpdated, notDestroyed);
    }
}
