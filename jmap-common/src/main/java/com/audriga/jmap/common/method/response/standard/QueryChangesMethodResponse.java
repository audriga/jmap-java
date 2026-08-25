package com.audriga.jmap.common.method.response.standard;

import com.audriga.jmap.common.entity.AddedItem;
import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.entity.TypedState;
import com.audriga.jmap.common.method.MethodResponse;
import java.util.List;

public interface QueryChangesMethodResponse<T extends Identifiable> extends MethodResponse {
    TypedState<T> getOldTypedQueryState();

    TypedState<T> getNewTypedQueryState();

    String accountId();

    String oldQueryState();

    String newQueryState();

    long total();

    String[] removed();

    List<AddedItem<String>> added();
}
