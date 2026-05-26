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

import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.entity.Comparator;
import com.audriga.jmap.common.entity.EmailSubmission;
import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.entity.query.EmailSubmissionQuery;
import com.audriga.jmap.common.method.call.standard.QueryMethodCall;
import lombok.NonNull;

@JmapMethod("EmailSubmission/query")
public class QueryEmailSubmissionMethodCall extends QueryMethodCall<EmailSubmission> {

    @lombok.Builder
    public QueryEmailSubmissionMethodCall(
            @NonNull String accountId,
            Filter<EmailSubmission> filter,
            Comparator[] sort,
            Long position,
            String anchor,
            Long anchorOffset,
            Long limit,
            Boolean calculateTotal) {
        super(accountId, filter, sort, position, anchor, anchorOffset, limit, calculateTotal);
    }

    public static class Builder {
        public Builder query(EmailSubmissionQuery query) {
            filter(query.filter);
            sort(query.sort);
            return this;
        }
    }
}
