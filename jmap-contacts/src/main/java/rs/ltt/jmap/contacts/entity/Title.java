package rs.ltt.jmap.contacts.entity;

import rs.ltt.jmap.annotation.Default;
import rs.ltt.jmap.annotation.Type;

@Type
public record Title(String name, @Default("\"title\"") String kind) {}
