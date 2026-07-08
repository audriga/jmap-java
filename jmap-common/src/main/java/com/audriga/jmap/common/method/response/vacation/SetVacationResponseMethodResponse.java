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
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.entity.VacationResponse;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import java.util.Map;
import lombok.Builder;

@JmapMethod("VacationResponse/set")
public class SetVacationResponseMethodResponse extends SetMethodResponse<VacationResponse> {

    @Builder
    public SetVacationResponseMethodResponse(
            String accountId,
            String oldState,
            String newState,
            Map<String, VacationResponse> created,
            Map<String, VacationResponse> updated,
            String[] destroyed,
            Map<String, SetError> notCreated,
            Map<String, SetError> notUpdated,
            Map<String, SetError> notDestroyed) {
        super(accountId, oldState, newState, created, updated, destroyed, notCreated, notUpdated, notDestroyed);
    }
}
