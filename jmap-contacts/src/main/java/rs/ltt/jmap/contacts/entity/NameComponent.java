package rs.ltt.jmap.contacts.entity;

import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Type;

@Type
public record NameComponent(
        String value, String kind, @Nullable String phonetic) {}
