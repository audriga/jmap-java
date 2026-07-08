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

package com.audriga.jmap.common.method.call.submission;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapImplicitNamespace;
import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.Request;
import com.audriga.jmap.common.entity.EmailSubmission;
import com.audriga.jmap.common.method.call.standard.SetMethodCall;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@JmapMethod("EmailSubmission/set")
@Getter
public class SetEmailSubmissionMethodCall extends SetMethodCall<EmailSubmission> {

    @JmapImplicitNamespace(Namespace.MAIL)
    private Map<String, Map<String, Object>> onSuccessUpdateEmail;

    private List<String> onSuccessDestroyEmail;

    @Builder
    public SetEmailSubmissionMethodCall(
            String accountId,
            String ifInState,
            Map<String, EmailSubmission> create,
            Map<String, Map<String, Object>> update,
            String[] destroy,
            Request.Invocation.ResultReference destroyReference,
            Map<String, Map<String, Object>> onSuccessUpdateEmail,
            List<String> onSuccessDestroyEmail) {
        super(accountId, ifInState, create, update, destroy, destroyReference);
        this.onSuccessUpdateEmail = onSuccessUpdateEmail;
        this.onSuccessDestroyEmail = onSuccessDestroyEmail;
    }
}
