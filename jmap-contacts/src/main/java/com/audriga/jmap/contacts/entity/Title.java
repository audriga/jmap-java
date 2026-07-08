package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Type;

@Type
public record Title(String name, @Default("\"title\"") String kind) {}
