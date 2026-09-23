package com.audriga.jmap.preferences.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.call.standard.SetMethodCall;
import com.audriga.jmap.preferences.entity.Preference;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("Preferences/set")
@RecordBuilder
public record SetPreferencesCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<String> ifInState,
        @Nullable Arg<Map<String, Preference>> create,
        @Nullable Arg<Map<String, Map<String, Object>>> update,
        @Nullable Arg<List<String>> destroy)
        implements SetMethodCall<Preference> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends SetPreferencesCallBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
