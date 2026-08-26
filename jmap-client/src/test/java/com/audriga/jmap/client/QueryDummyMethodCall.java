/*
 * Copyright 2020 Daniel Gultsch
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

package com.audriga.jmap.client;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.entity.Comparator;
import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.method.call.standard.QueryMethodCall;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

@RecordBuilder
public record QueryDummyMethodCall(
        @NonNull Arg<String> accountId,
        @Nullable Arg<Filter<Dummy>> filter,
        @Nullable Arg<List<Comparator>> sort,
        @Default("0") @Nullable Arg<Long> position,
        @Nullable Arg<String> anchor,
        @Default("0") @Nullable Arg<Long> anchorOffset,
        @Nullable Arg<Long> limit,
        @Default("false") @Nullable Arg<Boolean> calculateTotal)
        implements QueryMethodCall<Dummy> {
    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return Builder.of(this);
    }

    public static final class Builder extends QueryDummyMethodCallBuilder {
        @Override
        public Builder __this() {
            return this;
        }
    }
}
