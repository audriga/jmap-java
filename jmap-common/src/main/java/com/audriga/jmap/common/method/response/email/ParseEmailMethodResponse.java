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
import com.audriga.jmap.common.method.MethodResponse;
import com.google.common.base.MoreObjects;
import java.util.Map;
import lombok.Getter;

@JmapMethod("Email/parse")
@Getter
public class ParseEmailMethodResponse implements MethodResponse {

    private String accountId;
    private Map<String, Email> parsed;
    private String[] notParsable;
    private String[] notFound;

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("accountId", accountId)
                .add("parsed", parsed)
                .add("notParsable", notParsable)
                .add("notFound", notFound)
                .toString();
    }
}
