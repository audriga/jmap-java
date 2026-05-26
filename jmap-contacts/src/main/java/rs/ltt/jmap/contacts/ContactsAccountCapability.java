package rs.ltt.jmap.contacts;

import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.Namespace;
import rs.ltt.jmap.annotation.JmapAccountCapability;
import rs.ltt.jmap.common.entity.AccountCapability;

@JmapAccountCapability(namespace = Namespace.CONTACTS)
public record ContactsAccountCapability(@Nullable Long maxAddressBooksPerCard, boolean mayCreateAddressBook)
        implements AccountCapability {}
