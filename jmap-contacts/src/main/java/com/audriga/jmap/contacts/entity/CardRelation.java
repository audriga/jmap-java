package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import java.util.Set;

@lombok.Builder(toBuilder = true)
@Type("Relation")
public record CardRelation(Set<String> relation) {}
