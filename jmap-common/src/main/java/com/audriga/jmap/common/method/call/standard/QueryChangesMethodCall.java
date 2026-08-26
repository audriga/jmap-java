package com.audriga.jmap.common.method.call.standard;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.common.entity.Comparator;
import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.method.MethodCall;
import java.util.List;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public interface QueryChangesMethodCall<T extends Identifiable> extends MethodCall {
    @NonNull
    Arg<String> accountId();

    @Nullable
    Arg<Filter<T>> filter();

    @Nullable
    Arg<List<Comparator>> sort();

    @NonNull
    Arg<String> sinceQueryState();

    @Nullable
    Arg<Long> maxChanges();

    @Nullable
    Arg<String> upToId();

    @Default("false")
    @Nullable
    Arg<Boolean> calculateTotal();
}
