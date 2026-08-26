package com.audriga.jmap.contacts.method;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.Comparator;
import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.method.call.standard.QueryMethodCall;
import com.audriga.jmap.contacts.entity.ContactCard;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("ContactCard/query")
@RecordBuilder
public record QueryContactCardCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<Filter<ContactCard>> filter,
        @Nullable Arg<List<Comparator>> sort,
        @Default("0") @Nullable Arg<Long> position,
        @Nullable Arg<String> anchor,
        @Default("0") @Nullable Arg<Long> anchorOffset,
        @Nullable Arg<Long> limit,
        @Default("false") @Nullable Arg<Boolean> calculateTotal)
        implements QueryMethodCall<ContactCard> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryContactCardCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
