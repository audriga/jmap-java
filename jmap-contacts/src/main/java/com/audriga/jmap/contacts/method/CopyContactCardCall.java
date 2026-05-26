package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.method.call.standard.CopyMethodCall;
import com.audriga.jmap.contacts.entity.ContactCard;
import java.util.Map;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@JmapMethod("ContactCard/copy")
public class CopyContactCardCall extends CopyMethodCall<ContactCard> {
    @Builder
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
