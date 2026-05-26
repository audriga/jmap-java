package rs.ltt.jmap.contacts.method;

import java.util.Map;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.common.method.call.standard.CopyMethodCall;
import rs.ltt.jmap.contacts.entity.ContactCard;

@JmapMethod("ContactCard/copy")
public class CopyContactCardCall extends CopyMethodCall<ContactCard> {
    public CopyContactCardCall(
            String fromAccountId,
            @Nullable String ifFromInState,
            String accountId,
            @Nullable String ifInState,
            Map<String, ContactCard> create,
            @Nullable Boolean onSuccessDestroyOriginal,
            @Nullable String destroyFromIfInState) {
        super(
                fromAccountId,
                ifFromInState,
                accountId,
                ifInState,
                create,
                onSuccessDestroyOriginal,
                destroyFromIfInState);
    }
}
