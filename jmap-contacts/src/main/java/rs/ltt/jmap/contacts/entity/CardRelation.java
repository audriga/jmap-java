package rs.ltt.jmap.contacts.entity;

import java.util.Set;
import rs.ltt.jmap.annotation.Type;

@Type("Relation")
public record CardRelation(Set<String> relation) {}
