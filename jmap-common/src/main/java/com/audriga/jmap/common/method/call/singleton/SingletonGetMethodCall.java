package com.audriga.jmap.common.method.call.singleton;

import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.method.call.standard.GetMethodCall;
import java.util.List;
import org.jspecify.annotations.Nullable;

public interface SingletonGetMethodCall<T extends Identifiable> extends GetMethodCall<T> {
    Arg<List<String>> IDS = Arg.of(List.of("singleton"));

    @Override
    default @Nullable Arg<List<String>> ids() {
        return IDS;
    }
}
