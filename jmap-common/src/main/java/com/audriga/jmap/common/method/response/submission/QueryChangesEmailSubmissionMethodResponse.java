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

package com.audriga.jmap.common.method.response.submission;

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.AddedItem;
import com.audriga.jmap.common.entity.EmailSubmission;
import com.audriga.jmap.common.method.response.standard.QueryChangesMethodResponse;
import java.util.List;

@JmapMethod("EmailSubmission/queryChanges")
@RecordBuilder
public record QueryChangesEmailSubmissionMethodResponse(
        String accountId,
        String oldQueryState,
        String newQueryState,
        long total,
        List<String> removed,
        List<AddedItem<String>> added)
        implements QueryChangesMethodResponse<EmailSubmission> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryChangesEmailSubmissionMethodResponseBuilder {
        @Override
        protected Builder __this() {
            return this;
        }
    }
}
