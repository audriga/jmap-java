package rs.ltt.jmap.contacts.method;

import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.common.method.response.standard.CopyMethodResponse;
import rs.ltt.jmap.contacts.entity.ContactCard;

@JmapMethod("ContactCard/copy")
public class CopyContactCardResponse extends CopyMethodResponse<ContactCard> {}
