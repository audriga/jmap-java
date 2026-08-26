package com.audriga.jmap.common.method.call.standard;

import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.method.MethodCall;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface SetMethodCall<T extends Identifiable> extends MethodCall {
    @NonNull
    Arg<String> accountId();

    @Nullable
    Arg<String> ifInState();

    @Nullable
    Arg<Map<String, T>> create();

    @Nullable
    Arg<Map<String, Map<String, Object>>> update();

    @Nullable
    Arg<List<String>> destroy();
}
