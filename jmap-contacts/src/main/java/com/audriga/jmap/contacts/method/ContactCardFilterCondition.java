package com.audriga.jmap.contacts.method;

import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.entity.filter.FilterCondition;
import com.audriga.jmap.common.util.QueryStringUtils;
import com.audriga.jmap.contacts.entity.ContactCard;
import com.google.gson.annotations.SerializedName;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

public record ContactCardFilterCondition(
        @Nullable String inAddressBook,
        @Nullable String uid,
        @Nullable String hasMember,
        @Nullable String kind,
        @Nullable Instant createdBefore,
        @Nullable Instant createdAfter,
        @Nullable Instant updatedBefore,
        @Nullable Instant updatedAfter,
        @Nullable String text,
        @Nullable String name,
        @SerializedName("name/given") @Nullable String givenName,
        @SerializedName("name/surname") @Nullable String surname,
        @SerializedName("name/surname2") @Nullable String surname2,
        @Nullable String nickname,
        @Nullable String organization,
        @Nullable String email,
        @Nullable String phone,
        @Nullable String onlineService,
        @Nullable String address,
        @Nullable String note)
        implements FilterCondition<ContactCard> {
    @Override
    public int compareTo(Filter<ContactCard> o) {
        return toQueryString().compareTo(o.toQueryString());
    }

    @Override
    public String toQueryString() {
        return QueryStringUtils.toQueryString(
                L3_DIVIDER,
                L4_DIVIDER,
                inAddressBook,
                uid,
                hasMember,
                kind,
                createdBefore,
                createdAfter,
                updatedBefore,
                updatedAfter,
                text,
                name,
                givenName,
                surname,
                surname2,
                nickname,
                organization,
                email,
                phone,
                onlineService,
                address,
                note);
    }
}
