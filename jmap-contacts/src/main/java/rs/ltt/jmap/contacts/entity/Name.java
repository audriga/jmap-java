package rs.ltt.jmap.contacts.entity;

import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Default;
import rs.ltt.jmap.annotation.Type;

@Type
public record Name(
        @Nullable List<NameComponent> components,
        @Default("false") boolean isOrdered,
        @Nullable String defaultSeparator,
        @Nullable String full,
        @Nullable Map<String, String> sortAs,
        @Nullable String phoneticScript,
        @Nullable String phoneticSystem) {}
