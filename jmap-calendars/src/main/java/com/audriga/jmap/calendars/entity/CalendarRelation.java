package com.audriga.jmap.calendars.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Type;
import java.util.Set;

@Type("Relation")
public record CalendarRelation(@Default("{}") Set<String> relation) {}
