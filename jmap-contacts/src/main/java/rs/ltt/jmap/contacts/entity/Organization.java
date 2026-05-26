package rs.ltt.jmap.contacts.entity;

import java.util.List;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Type;

@Type
public record Organization(
        @Nullable String name,
        @Nullable List<OrgUnit> units,
        @Nullable String sortAs,
        @Nullable Contexts contexts) {}
