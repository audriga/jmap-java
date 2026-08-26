package com.audriga.jmap.common.method.response.standard;

import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.entity.SetError;
import com.audriga.jmap.common.method.MethodResponse;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface CopyMethodResponse<T extends Identifiable> extends MethodResponse {
    @NonNull
    String fromAccountId();

    @NonNull
    String accountId();

    @Nullable
    String oldState();

    @NonNull
    String newState();

    @Nullable
    Map<String, T> created();

    @Nullable
    Map<String, SetError> notCreated();
}
