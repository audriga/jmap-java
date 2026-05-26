package rs.ltt.jmap.contacts.entity;

import java.util.Set;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Type;

@Type
public record Phone(
        String number,
        @Nullable Set<String> features,
        @Nullable Contexts contexts,
        @Nullable Integer pref,
        @Nullable String label) {}
