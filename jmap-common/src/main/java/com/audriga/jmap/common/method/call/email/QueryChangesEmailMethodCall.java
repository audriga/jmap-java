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

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.Comparator;
import com.audriga.jmap.common.entity.Email;
import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.entity.query.EmailQuery;
import com.audriga.jmap.common.method.call.standard.QueryChangesMethodCall;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("Email/queryChanges")
@RecordBuilder
public record QueryChangesEmailMethodCall(
        @NonNull String accountId,
        @Nullable Filter<Email> filter,
        @Nullable List<Comparator> sort,
        @NonNull String sinceQueryState,
        @Nullable Long maxChanges,
        @Nullable String upToId,
        @Default("false") @Nullable Boolean calculateTotal,
        @Default("false") @Nullable Boolean collapseThreads)
        implements QueryChangesMethodCall<Email> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryChangesEmailMethodCallBuilder {
        public Builder query(EmailQuery query) {
            filter(query.filter);
            sort(query.sort);
            collapseThreads(query.collapseThreads);
            return this;
        }

        @Override
        protected Builder __this() {
            return this;
        }
    }
}
