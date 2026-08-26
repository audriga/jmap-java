package com.audriga.jmap.common.method.response.standard;

import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.entity.TypedState;
import com.audriga.jmap.common.method.MethodResponse;
import java.util.List;
import org.jspecify.annotations.NonNull;

public interface ChangesMethodResponse<T extends Identifiable> extends MethodResponse {
    @NonNull
    String accountId();

    @NonNull
    String oldState();

    default @NonNull TypedState<T> typedOldState() {
        return TypedState.of(oldState());
    }

    @NonNull
    String newState();

    default @NonNull TypedState<T> typedNewState() {
        return TypedState.of(newState());
    }

    boolean hasMoreChanges();

    @NonNull
    List<String> created();

    @NonNull
    List<String> updated();

    @NonNull
    List<String> destroyed();
}
