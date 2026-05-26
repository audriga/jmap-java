package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Type;
import java.util.Set;
import lombok.Builder;

@Builder(toBuilder = true)
@Type("Relation")
public record CardRelation(Set<String> relation) {}
