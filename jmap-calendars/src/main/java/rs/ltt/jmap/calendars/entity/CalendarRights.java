package rs.ltt.jmap.calendars.entity;

public record CalendarRights(
        boolean mayReadFreeBusy,
        boolean mayReadItems,
        boolean mayWriteAll,
        boolean mayWriteOwn,
        boolean mayUpdatePrivate,
        boolean mayRSVP,
        boolean mayShare,
        boolean mayDelete) {}
