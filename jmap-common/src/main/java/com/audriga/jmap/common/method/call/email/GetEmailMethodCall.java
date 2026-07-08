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

package com.audriga.jmap.common.method.call.email;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.Request;
import com.audriga.jmap.common.entity.Email;
import com.audriga.jmap.common.method.call.standard.GetMethodCall;
import lombok.Builder;
import lombok.Getter;

@JmapMethod("Email/get")
@Getter
public class GetEmailMethodCall extends GetMethodCall<Email> {

    private String[] bodyProperties;
    private Boolean fetchTextBodyValues;
    private Boolean fetchHTMLBodyValues;
    private Boolean fetchAllBodyValues;
    private Long maxBodyValueBytes;

    @Builder
    public GetEmailMethodCall(
            String accountId,
            String[] ids,
            String[] properties,
            Request.Invocation.ResultReference idsReference,
            String[] bodyProperties,
            Boolean fetchTextBodyValues,
            Boolean fetchHTMLBodyValues,
            Boolean fetchAllBodyValues,
            Long maxBodyValueBytes) {
        super(accountId, ids, properties, idsReference);
        this.bodyProperties = bodyProperties;
        this.fetchTextBodyValues = fetchTextBodyValues;
        this.fetchHTMLBodyValues = fetchHTMLBodyValues;
        this.fetchAllBodyValues = fetchAllBodyValues;
        this.maxBodyValueBytes = maxBodyValueBytes;
    }
}
