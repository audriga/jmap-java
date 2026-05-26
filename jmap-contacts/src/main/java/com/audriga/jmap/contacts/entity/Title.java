package com.audriga.jmap.contacts.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Type;
import lombok.Builder;

@Builder(toBuilder = true)
@Type
public record Title(String name, @Default("\"title\"") String kind) {}
