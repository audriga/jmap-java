package com.audriga.jmap.calendars.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Immutable;
import com.audriga.jmap.annotation.ServerSet;
import com.audriga.jmap.common.entity.Identifiable;
import java.net.URI;
import lombok.Builder;

@Builder(toBuilder = true)
public record ParticipantIdentity(
        @Immutable @ServerSet String id,
        @Default("\"\"") String name,
        URI calendarAddress,
        @ServerSet Boolean isDefault)
        implements Identifiable {}
