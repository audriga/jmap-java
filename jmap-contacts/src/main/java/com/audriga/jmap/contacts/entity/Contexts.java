package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Inline;
import java.util.Set;

@Inline
public record Contexts(Set<String> values) {}
