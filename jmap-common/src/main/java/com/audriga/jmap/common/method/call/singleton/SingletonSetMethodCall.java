package com.audriga.jmap.common.method.call.singleton;

import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.method.call.standard.SetMethodCall;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;

public interface SingletonSetMethodCall<T extends Identifiable> extends SetMethodCall<T> {
    @Override
    default @Nullable Arg<Map<String, T>> create() {
        return null;
    }

    @Override
    default @Nullable Arg<Map<String, Map<String, Object>>> update() {
        final var single = updateSingle();
        return single != null ? Arg.of(Map.of("singleton", single)) : null;
    }

    @Nullable
    Map<String, Object> updateSingle();

    @Override
    default @Nullable Arg<List<String>> destroy() {
        return null;
    }

    static @Nullable Map<String, Object> asUpdateSingle(@Nullable Arg<Map<String, Map<String, Object>>> update) {
        if (update == null) return null;
        if (!(update instanceof Arg.Value<Map<String, Map<String, Object>>> v)) {
            throw new IllegalArgumentException("expected update to singleton ID, result references are not supported");
        }
        if (!Set.of("singleton").equals(v.value().keySet())) {
            throw new IllegalArgumentException(
                    "can only update ID 'singleton', found " + v.value().keySet());
        }
        return v.value().get("singleton");
    }
}
