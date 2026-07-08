/*
 * Copyright 2020 cketti
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

package com.audriga.jmap.common.method.response.email;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.entity.Email;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.MethodResponse;
import com.google.common.base.MoreObjects;
import java.util.Map;
import lombok.Getter;

@JmapMethod("Email/import")
@Getter
public class ImportEmailMethodResponse implements MethodResponse {

    private String accountId;
    private String oldState;
    private String newState;
    private Map<String, Email> created;
    private Map<String, SetError> notCreated;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("accountId", accountId)
                .add("oldState", oldState)
                .add("newState", newState)
                .add("created", created)
                .add("notCreated", notCreated)
                .toString();
    }
}
