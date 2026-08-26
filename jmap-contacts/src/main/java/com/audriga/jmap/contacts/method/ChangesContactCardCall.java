package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.call.standard.ChangesMethodCall;
import com.audriga.jmap.contacts.entity.ContactCard;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("ContactCard/changes")
@RecordBuilder
public record ChangesContactCardCall(
        @NonNull Arg<String> accountId,
        @NonNull Arg<String> sinceState,
        @Nullable Arg<Long> maxChanges) implements ChangesMethodCall<ContactCard> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends ChangesContactCardCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
