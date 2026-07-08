package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import com.audriga.jmap.contacts.entity.ContactCard;
import java.util.Map;

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
