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

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.Comparator;
import com.audriga.jmap.common.entity.Mailbox;
import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.entity.query.MailboxQuery;
import com.audriga.jmap.common.method.call.standard.QueryMethodCall;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@JmapMethod("Mailbox/query")
@RecordBuilder
public record QueryMailboxMethodCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<Filter<Mailbox>> filter,
        @Nullable Arg<List<Comparator>> sort,
        @Default("0") @Nullable Arg<Long> position,
        @Nullable Arg<String> anchor,
        @Default("0") @Nullable Arg<Long> anchorOffset,
        @Nullable Arg<Long> limit,
        @Default("false") @Nullable Arg<Boolean> calculateTotal,
        @Default("false") @Nullable Arg<Boolean> sortAsTree,
        @Default("false") @Nullable Arg<Boolean> filterAsTree)
        implements QueryMethodCall<Mailbox> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryMailboxMethodCallBuilder {
        public Builder query(MailboxQuery query) {
            filter(query.filter);
            sort(query.sort);
            sortAsTree(query.sortAsTree);
            filterAsTree(query.filterAsTree);
            return this;
        }

        @Override
        protected Builder __this() {
            return this;
        }
    }
}
