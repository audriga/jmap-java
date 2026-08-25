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
    String accountId();

    @Nullable
    Filter<T> filter();

    @Nullable
    List<Comparator> sort();

    @NonNull
    String sinceQueryState();

    @Nullable
    Long maxChanges();

    @Nullable
    String upToId();

    @Default("false")
    @Nullable
    Boolean calculateTotal();
}
