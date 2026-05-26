package rs.ltt.jmap.contacts.entity;

import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Type;

@Type
public record EmailAddress(
        String address,
        @Nullable Contexts contexts,
        @Nullable Integer pref,
        @Nullable String label) {}
