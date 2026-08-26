package com.audriga.jmap.common.method.call.standard;

import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.method.MethodCall;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface ChangesMethodCall<T extends Identifiable> extends MethodCall {
    @NonNull
    Arg<String> accountId();

    @NonNull
    Arg<String> sinceState();

    @Nullable
    Arg<Long> maxChanges();
}
