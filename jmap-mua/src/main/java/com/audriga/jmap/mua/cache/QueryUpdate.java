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

package com.audriga.jmap.mua.cache;

import com.audriga.jmap.common.entity.AddedItem;
import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.entity.TypedState;
import com.audriga.jmap.common.method.response.standard.QueryChangesMethodResponse;
import com.google.common.base.MoreObjects;
import java.util.List;

public class QueryUpdate<T extends Identifiable, U> extends AbstractUpdate<T> {

    private final List<String> removed;

    private final List<AddedItem<U>> added;

    private final Long total;

    private QueryUpdate(
            final TypedState<T> oldState,
            final TypedState<T> newState,
            final List<String> removed,
            final List<AddedItem<U>> added,
            final Long total) {
        super(oldState, newState, false);
        this.removed = removed;
        this.added = added;
        this.total = total;
    }

    public static <T extends Identifiable, U> QueryUpdate<T, U> of(
            QueryChangesMethodResponse<T> queryChangesMethodResponse, List<AddedItem<U>> added) {
        return new QueryUpdate<>(
                queryChangesMethodResponse.oldTypedQueryState(),
                queryChangesMethodResponse.newTypedQueryState(),
                queryChangesMethodResponse.removed(),
                added,
                queryChangesMethodResponse.total());
    }

    public List<String> removed() {
        return this.removed;
    }

    public List<AddedItem<U>> added() {
        return this.added;
    }

    public Long total() {
        return this.total;
    }

    @Override
    public boolean hasChanges() {
        final boolean modifiedItems = removed.size() + added.size() > 0;
        return modifiedItems || hasStateChange();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("removed", removed)
                .add("added", added)
                .toString();
    }
}
