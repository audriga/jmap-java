package rs.ltt.jmap.contacts.entity;

import java.util.Map;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Type;

@Type
public record SpeakToAs(
        @Nullable String grammaticalGender, @Nullable Map<String, Pronouns> pronouns) {}
