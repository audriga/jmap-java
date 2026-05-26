package rs.ltt.jmap.contacts.method;

import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.common.method.response.standard.GetMethodResponse;
import rs.ltt.jmap.contacts.entity.ContactCard;

@JmapMethod("ContactCard/get")
public class GetContactCardResponse extends GetMethodResponse<ContactCard> {
    public GetContactCardResponse(String accountId, String state, String[] notFound, ContactCard[] list) {
        super(accountId, state, notFound, list);
    }
}
