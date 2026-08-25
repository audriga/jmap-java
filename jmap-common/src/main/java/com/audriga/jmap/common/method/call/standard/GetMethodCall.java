package com.audriga.jmap.common.method.call.standard;

import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.method.MethodCall;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface GetMethodCall<T extends Identifiable> extends MethodCall {
    @NonNull
    String accountId();

    @Nullable
    String[] ids();

    @Nullable
    String[] properties();
}
