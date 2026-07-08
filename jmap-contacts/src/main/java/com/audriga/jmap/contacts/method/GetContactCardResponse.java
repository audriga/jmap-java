package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;
import com.audriga.jmap.contacts.entity.ContactCard;

@JmapMethod("ContactCard/get")
public class GetContactCardResponse extends GetMethodResponse<ContactCard> {
    public GetContactCardResponse(String accountId, String state, String[] notFound, ContactCard[] list) {
        super(accountId, state, notFound, list);
    }
}
