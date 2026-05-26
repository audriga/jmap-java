package rs.ltt.jmap.calendars.entity;

import java.net.URI;
import rs.ltt.jmap.annotation.Default;
import rs.ltt.jmap.annotation.Immutable;
import rs.ltt.jmap.annotation.ServerSet;
import rs.ltt.jmap.common.entity.Identifiable;

public record ParticipantIdentity(
        @Immutable @ServerSet String id,
        @Default("\"\"") String name,
        URI calendarAddress,
        @ServerSet Boolean isDefault)
        implements Identifiable {}
