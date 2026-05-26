package rs.ltt.jmap.calendars.entity;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import java.time.Duration;
import java.time.Instant;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Default;
import rs.ltt.jmap.annotation.Type;

@Type
public record Alert(Trigger trigger, @Nullable Instant acknowledged) {
    @Type("OffsetTrigger")
    public sealed interface Trigger {}

    @Type
    public record OffsetTrigger(
            Duration offset, @Default("\"start\"") RelativeTo relativeTo) implements Trigger {
        enum RelativeTo {
            @SerializedName("start")
            START,
            @SerializedName("end")
            END
        }
    }

    @Type
    public record AbsoluteTrigger(Instant when) implements Trigger {}

    public record UnknownTrigger(String type, JsonObject data) implements Trigger, Type.Dynamic<JsonObject> {}
}
