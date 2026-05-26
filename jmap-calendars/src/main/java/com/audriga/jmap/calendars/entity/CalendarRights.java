package com.audriga.jmap.calendars.entity;

@lombok.Builder(toBuilder = true)
public record CalendarRights(
        boolean mayReadFreeBusy,
        boolean mayReadItems,
        boolean mayWriteAll,
        boolean mayWriteOwn,
        boolean mayUpdatePrivate,
        boolean mayRSVP,
        boolean mayShare,
        boolean mayDelete) {}
