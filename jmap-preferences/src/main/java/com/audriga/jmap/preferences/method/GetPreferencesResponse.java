package com.audriga.jmap.preferences.method;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;
import com.audriga.jmap.preferences.entity.Preference;
import java.util.List;

@JmapMethod("Preferences/get")
@RecordBuilder
public record GetPreferencesResponse(String accountId, String state, List<Preference> list, List<String> notFound)
        implements GetMethodResponse<Preference> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends GetPreferencesResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
