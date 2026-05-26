package rs.ltt.jmap.contacts.method;

import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.common.Request;
import rs.ltt.jmap.common.method.call.standard.GetMethodCall;
import rs.ltt.jmap.contacts.entity.ContactCard;

@JmapMethod("ContactCard/get")
public class GetContactCardCall extends GetMethodCall<ContactCard> {
    public GetContactCardCall(
            String accountId,
            @Nullable String[] ids,
            @Nullable String[] properties,
            Request.Invocation.@Nullable ResultReference idsReference) {
        super(accountId, ids, properties, idsReference);
    }
}
