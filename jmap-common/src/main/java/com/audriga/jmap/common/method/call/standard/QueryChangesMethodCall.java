package com.audriga.jmap.common.method.call.standard;

import com.audriga.jmap.common.entity.Comparator;
import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.entity.filter.Filter;
import com.audriga.jmap.common.method.MethodCall;

public interface QueryChangesMethodCall<T extends Identifiable> extends MethodCall {
    String accountId();

    Filter<T> filter();

    Comparator[] sort();

    String sinceQueryState();

    Long maxChanges();

    String upToId();

    Boolean calculateTotal();
}
