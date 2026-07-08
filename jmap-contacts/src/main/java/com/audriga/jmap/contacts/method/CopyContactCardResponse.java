package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.method.response.standard.CopyMethodResponse;
import com.audriga.jmap.contacts.entity.ContactCard;

@JmapMethod("ContactCard/copy")
public class CopyContactCardResponse extends CopyMethodResponse<ContactCard> {}
