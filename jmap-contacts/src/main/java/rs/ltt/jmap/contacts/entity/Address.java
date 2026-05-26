package rs.ltt.jmap.contacts.entity;

import java.net.URI;
import java.util.List;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Default;
import rs.ltt.jmap.annotation.Type;

@Type
public record Address(
        @Nullable List<AddressComponent> components,
        @Default("false") boolean isOrdered,
        @Nullable String countryCode,
        @Nullable URI coordinates,
        @Nullable String timeZone,
        @Nullable Contexts contexts,
        @Nullable String full,
        @Nullable String defaultSeparator,
        @Nullable Integer pref,
        @Nullable String phoneticScript,
        @Nullable String phoneticSystem) {}
