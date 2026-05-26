package com.audriga.jmap.calendars.entity;

import lombok.Builder;

@Builder(toBuilder = true)
public record CalendarRights(
        boolean mayReadFreeBusy,
        boolean mayReadItems,
        boolean mayWriteAll,
        boolean mayWriteOwn,
        boolean mayUpdatePrivate,
        boolean mayRSVP,
        boolean mayShare,
        boolean mayDelete) {}
