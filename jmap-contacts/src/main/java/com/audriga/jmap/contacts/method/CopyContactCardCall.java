package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.call.standard.CopyMethodCall;
import com.audriga.jmap.contacts.entity.ContactCard;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("ContactCard/copy")
@RecordBuilder
public record CopyContactCardCall(
        @NonNull Arg<String> fromAccountId,
        @Nullable Arg<String> ifFromInState,
        @NonNull Arg<String> accountId,
        @Nullable Arg<String> ifInState,
        @NonNull Arg<Map<String, ContactCard>> create,
        @Default("false") @Nullable Arg<Boolean> onSuccessDestroyOriginal,
        @Nullable Arg<String> destroyFromIfInState)
        implements CopyMethodCall<ContactCard> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends CopyContactCardCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
