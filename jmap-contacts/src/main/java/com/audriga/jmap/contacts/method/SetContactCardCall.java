package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.Request;
import com.audriga.jmap.common.method.call.standard.SetMethodCall;
import com.audriga.jmap.contacts.entity.ContactCard;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@JmapMethod("ContactCard/set")
public class SetContactCardCall extends SetMethodCall<ContactCard> {
    public SetContactCardCall(
            String accountId,
            @Nullable String ifInState,
            @Nullable Map<String, ContactCard> create,
            @Nullable Map<String, Map<String, Object>> update,
            String @Nullable [] destroy,
            Request.Invocation.@Nullable ResultReference destroyReference) {
        super(accountId, ifInState, create, update, destroy, destroyReference);
    }
}
