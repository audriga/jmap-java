package rs.ltt.jmap.calendars.entity;

import java.util.Set;
import rs.ltt.jmap.annotation.Default;
import rs.ltt.jmap.annotation.Type;

@Type("Relation")
public record CalendarRelation(@Default("{}") Set<String> relation) {}
