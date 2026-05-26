package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.method.call.standard.ChangesMethodCall;
import com.audriga.jmap.contacts.entity.ContactCard;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@JmapMethod("ContactCard/changes")
public class ChangesContactCardCall extends ChangesMethodCall<ContactCard> {
    @Builder
    public ChangesContactCardCall(String accountId, String sinceState, @Nullable Long maxChanges) {
        super(accountId, sinceState, maxChanges);
    }
}
