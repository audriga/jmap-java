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

package com.audriga.jmap.common.method.call.mailbox;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.Request;
import com.audriga.jmap.common.entity.Mailbox;
import com.audriga.jmap.common.method.call.standard.GetMethodCall;
import com.google.common.base.Preconditions;
import com.google.gson.annotations.SerializedName;

@JmapMethod("Mailbox/get")
public class GetMailboxMethodCall extends GetMethodCall<Mailbox> {

    @SerializedName("#properties")
    private Request.Invocation.ResultReference propertiesReference;

    @lombok.Builder
    public GetMailboxMethodCall(
            String accountId,
            String[] ids,
            String[] properties,
            Request.Invocation.ResultReference idsReference,
            Request.Invocation.ResultReference propertiesReference) {
        super(accountId, ids, properties, idsReference);
        Preconditions.checkArgument(
                properties == null || propertiesReference == null,
                "Can't set both 'properties' and 'propertiesReference'");
        this.propertiesReference = propertiesReference;
    }
}
