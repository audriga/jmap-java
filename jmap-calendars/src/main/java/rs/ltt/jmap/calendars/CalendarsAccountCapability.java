package rs.ltt.jmap.calendars;

import java.time.Duration;
import java.time.Instant;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.Namespace;
import rs.ltt.jmap.annotation.JmapAccountCapability;

@JmapAccountCapability(namespace = Namespace.CALENDARS)
public record CalendarsAccountCapability(
        @Nullable Long maxCalendarsPerEvent,
        Instant minDateTime,
        Instant maxDateTime,
        Duration maxExpandedQueryDuration,
        @Nullable Long maxParticipantsPerEvent,
        boolean mayCreateCalendar) {}
