package rs.ltt.jmap.contacts.entity;

import java.util.Map;
import lombok.Builder;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Default;
import rs.ltt.jmap.annotation.Immutable;
import rs.ltt.jmap.annotation.ServerSet;
import rs.ltt.jmap.common.entity.Identifiable;

@Builder
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
