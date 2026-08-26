package com.audriga.jmap.common.method.response.standard;

import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.entity.TypedState;
import com.audriga.jmap.common.method.MethodResponse;
import java.util.List;
import org.jspecify.annotations.Nullable;

public interface QueryMethodResponse<T extends Identifiable> extends MethodResponse {
    String accountId();

    String queryState();

    default TypedState<T> typedQueryState() {
        return TypedState.of(queryState());
    }

    boolean canCalculateChanges();

    Long position();

    List<String> ids();

    @Nullable
    Long total();

    @Nullable
    Long limit();
}
