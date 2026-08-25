package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.method.ResultReference;
import com.audriga.jmap.common.method.call.standard.AbstractGetMethodCall;
import com.audriga.jmap.contacts.entity.ContactCard;
import org.jspecify.annotations.Nullable;

@JmapMethod("ContactCard/get")
public class GetContactCardCall extends AbstractGetMethodCall<ContactCard> {
    @lombok.Builder
    public GetContactCardCall(
            String accountId,
            @Nullable String[] ids,
            @Nullable String[] properties,
            @Nullable ResultReference idsReference) {
        super(accountId, ids, properties, idsReference);
    }
}
