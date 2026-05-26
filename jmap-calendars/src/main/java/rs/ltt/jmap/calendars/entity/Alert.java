package rs.ltt.jmap.calendars.entity;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import java.time.Instant;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Default;
import rs.ltt.jmap.annotation.Inline;
import rs.ltt.jmap.annotation.Type;
import rs.ltt.jmap.common.DateTimePeriod;

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
