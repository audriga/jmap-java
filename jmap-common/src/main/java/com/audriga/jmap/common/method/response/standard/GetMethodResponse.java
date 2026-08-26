package com.audriga.jmap.common.method.response.standard;

import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.entity.TypedState;
import com.audriga.jmap.common.method.MethodResponse;
import java.util.List;

public interface GetMethodResponse<T extends Identifiable> extends MethodResponse {
    String accountId();

    String state();

    default TypedState<T> typedState() {
        return TypedState.of(state());
    }

    List<T> list();

    List<String> notFound();
}
