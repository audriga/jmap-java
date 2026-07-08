package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.method.response.standard.QueryMethodResponse;
import com.audriga.jmap.contacts.entity.ContactCard;

@JmapMethod("ContactCard/query")
public class QueryContactCardResponse extends QueryMethodResponse<ContactCard> {
    public QueryContactCardResponse(
            String accountId,
            String queryState,
            boolean canCalculateChanges,
            Long position,
            String[] ids,
            Long total,
            Long limit) {
        super(accountId, queryState, canCalculateChanges, position, ids, total, limit);
    }
}
