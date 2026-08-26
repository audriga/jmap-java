package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;
import com.audriga.jmap.contacts.entity.ContactCard;
import java.util.List;

@JmapMethod("ContactCard/get")
@RecordBuilder
public record GetContactCardResponse(String accountId, String state, List<ContactCard> list, List<String> notFound)
        implements GetMethodResponse<ContactCard> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetContactCardResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
