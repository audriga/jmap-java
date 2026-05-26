package rs.ltt.jmap.calendars;

import java.time.Instant;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.Namespace;
import rs.ltt.jmap.annotation.JmapAccountCapability;
import rs.ltt.jmap.common.DateTimePeriod;
import rs.ltt.jmap.common.entity.AccountCapability;

@JmapAccountCapability(namespace = Namespace.CALENDARS)
public record CalendarsAccountCapability(
        @Nullable Long maxCalendarsPerEvent,
        Instant minDateTime,
        Instant maxDateTime,
        DateTimePeriod maxExpandedQueryDuration,
        @Nullable Long maxParticipantsPerEvent,
        boolean mayCreateCalendar)
        implements AccountCapability {}
