package com.audriga.jmap.contacts;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapAccountCapability;
import com.audriga.jmap.common.entity.AccountCapability;
import org.jspecify.annotations.Nullable;

@JmapAccountCapability(namespace = Namespace.CONTACTS)
@lombok.Builder(toBuilder = true)
public record ContactsAccountCapability(@Nullable Long maxAddressBooksPerCard, boolean mayCreateAddressBook)
        implements AccountCapability {}
