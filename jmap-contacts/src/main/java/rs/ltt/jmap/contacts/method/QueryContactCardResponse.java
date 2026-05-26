package rs.ltt.jmap.contacts.method;

import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.common.method.response.standard.QueryMethodResponse;
import rs.ltt.jmap.contacts.entity.ContactCard;

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
