package com.audriga.jmap.calendars;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapAccountCapability;
import com.audriga.jmap.common.DateTimePeriod;
import com.audriga.jmap.common.entity.AccountCapability;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

@JmapAccountCapability(namespace = Namespace.CALENDARS)
public record CalendarsAccountCapability(
        @Nullable Long maxCalendarsPerEvent,
        Instant minDateTime,
        Instant maxDateTime,
        DateTimePeriod maxExpandedQueryDuration,
        @Nullable Long maxParticipantsPerEvent,
        boolean mayCreateCalendar)
        implements AccountCapability {}
