/*
 * Copyright 2019 Daniel Gultsch
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.audriga.jmap.common.method.response.vacation;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.entity.VacationResponse;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@JmapMethod("VacationResponse/set")
@RecordBuilder
public record SetVacationResponseMethodResponse(
        String accountId,
        @Nullable String oldState,
        String newState,
        @Nullable Map<String, VacationResponse> created,
        @Nullable Map<String, VacationResponse> updated,
        @Nullable List<String> destroyed,
        @Nullable Map<String, SetError> notCreated,
        @Nullable Map<String, SetError> notUpdated,
        @Nullable Map<String, SetError> notDestroyed)
        implements SetMethodResponse<VacationResponse> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends SetVacationResponseMethodResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
