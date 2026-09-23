package com.audriga.jmap.preferences.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import com.audriga.jmap.preferences.entity.Preference;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@JmapMethod("Preferences/set")
@RecordBuilder
public record SetPreferencesResponse(
        String accountId,
        @Nullable String oldState,
        String newState,
        @Nullable Map<String, Preference> created,
        @Nullable Map<String, Preference> updated,
        @Nullable List<String> destroyed,
        @Nullable Map<String, SetError> notCreated,
        @Nullable Map<String, SetError> notUpdated,
        @Nullable Map<String, SetError> notDestroyed)
        implements SetMethodResponse<Preference> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends SetPreferencesResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
