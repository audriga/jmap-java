package com.audriga.jmap.calendars.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Inline;
import com.audriga.jmap.annotation.Type;
import com.audriga.jmap.common.DateTimePeriod;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import java.time.Instant;
import org.jspecify.annotations.Nullable;

@Type
public record Alert(Trigger trigger, @Nullable Instant acknowledged) {
    @Type("OffsetTrigger")
    public sealed interface Trigger {}

    @Type
    public record OffsetTrigger(
            DateTimePeriod offset, @Default("\"start\"") RelativeTo relativeTo) implements Trigger {
        enum RelativeTo {
            @SerializedName("start")
            START,
            @SerializedName("end")
            END
        }
    }

    @Type
    public record AbsoluteTrigger(Instant when) implements Trigger {}

    @Type.Unknown
    @Inline
    public record UnknownTrigger(JsonObject data) implements Trigger {}
}
