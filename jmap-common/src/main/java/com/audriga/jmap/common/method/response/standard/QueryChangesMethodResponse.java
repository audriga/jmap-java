package com.audriga.jmap.common.method.response.standard;

import com.audriga.jmap.common.entity.AddedItem;
import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.entity.TypedState;
import com.audriga.jmap.common.method.MethodResponse;
import java.util.List;

public interface QueryChangesMethodResponse<T extends Identifiable> extends MethodResponse {
    String accountId();

    String oldQueryState();

    default TypedState<T> oldTypedQueryState() {
        return TypedState.of(oldQueryState());
    }

    String newQueryState();

    default TypedState<T> newTypedQueryState() {
        return TypedState.of(newQueryState());
    }

    long total();

    List<String> removed();

    List<AddedItem<String>> added();
}
