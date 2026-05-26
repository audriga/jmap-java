package rs.ltt.jmap.contacts.method;

import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.JmapMethod;
import rs.ltt.jmap.common.entity.Comparator;
import rs.ltt.jmap.common.entity.filter.Filter;
import rs.ltt.jmap.common.method.call.standard.QueryMethodCall;
import rs.ltt.jmap.contacts.entity.ContactCard;

@JmapMethod("ContactCard/query")
public class QueryContactCardCall extends QueryMethodCall<ContactCard> {
    public QueryContactCardCall(
            String accountId,
            @Nullable Filter<ContactCard> filter,
            Comparator @Nullable [] sort,
            @Nullable Long position,
            @Nullable String anchor,
            @Nullable Long anchorOffset,
            @Nullable Long limit,
            @Nullable Boolean calculateTotal) {
        super(accountId, filter, sort, position, anchor, anchorOffset, limit, calculateTotal);
    }
}
