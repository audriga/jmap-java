package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.entity.Comparator;
import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.method.call.standard.QueryMethodCall;
import com.audriga.jmap.contacts.entity.ContactCard;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@JmapMethod("ContactCard/query")
public class QueryContactCardCall extends QueryMethodCall<ContactCard> {
    @Builder
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
