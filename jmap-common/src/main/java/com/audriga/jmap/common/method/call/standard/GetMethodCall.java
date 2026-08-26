package com.audriga.jmap.common.method.call.standard;

import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.method.MethodCall;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface GetMethodCall<T extends Identifiable> extends MethodCall {
    @NonNull
    Arg<String> accountId();

    @Nullable
    Arg<List<String>> ids();

    @Nullable
    Arg<List<String>> properties();
}
