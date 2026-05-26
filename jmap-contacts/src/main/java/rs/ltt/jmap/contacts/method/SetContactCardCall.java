package rs.ltt.jmap.contacts.method;

import java.util.Map;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.common.Request;
import rs.ltt.jmap.common.method.call.standard.SetMethodCall;
import rs.ltt.jmap.contacts.entity.ContactCard;

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
