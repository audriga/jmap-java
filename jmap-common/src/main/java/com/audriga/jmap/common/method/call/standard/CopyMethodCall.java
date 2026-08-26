package com.audriga.jmap.common.method.call.standard;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.method.MethodCall;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface CopyMethodCall<T extends Identifiable> extends MethodCall {
    @NonNull
    Arg<String> fromAccountId();

    @Nullable
    Arg<String> ifFromInState();

    @NonNull
    Arg<String> accountId();

    @Nullable
    Arg<String> ifInState();

    @NonNull
    Arg<Map<String, T>> create();

    @Default("false")
    @Nullable
    Arg<Boolean> onSuccessDestroyOriginal();

    @Nullable
    Arg<String> destroyFromIfInState();
}
