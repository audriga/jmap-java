package rs.ltt.jmap.contacts.entity;

import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Type;

@Type
public record LanguagePref(
        String language,
        @Nullable Contexts contexts,
        @Nullable Integer pref) {}
