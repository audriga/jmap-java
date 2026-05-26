package rs.ltt.jmap.contacts.entity;

import java.util.Set;
import rs.ltt.jmap.annotation.Inline;

@Inline
public record Contexts(Set<String> values) {}
