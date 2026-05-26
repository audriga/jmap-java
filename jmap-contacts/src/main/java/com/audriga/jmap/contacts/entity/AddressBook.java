package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Immutable;
import com.audriga.jmap.annotation.ServerSet;
import com.audriga.jmap.common.entity.Identifiable;
import java.util.Map;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder(toBuilder = true)
public record AddressBook(
        @Immutable @ServerSet String id,
        String name,
        @Nullable String description,
        @Default("0") int sortOrder,
        @ServerSet Boolean isDefault,
        boolean isSubscribed,
        @Nullable Map<String, AddressBookRights> shareWith,
        @ServerSet AddressBookRights myRights)
        implements Identifiable {}
