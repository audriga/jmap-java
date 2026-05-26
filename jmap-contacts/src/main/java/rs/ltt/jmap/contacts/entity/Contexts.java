package rs.ltt.jmap.contacts.entity;

import rs.ltt.jmap.annotation.Inline;

import java.util.Set;

@Inline
public record Contexts(Set<String> values) {}
