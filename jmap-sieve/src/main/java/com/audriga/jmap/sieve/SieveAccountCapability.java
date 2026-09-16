package com.audriga.jmap.sieve;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapAccountCapability;
import com.audriga.jmap.common.entity.AccountCapability;
import org.jspecify.annotations.Nullable;

@JmapAccountCapability(namespace = Namespace.SIEVE)
@lombok.Builder(toBuilder = true)
public record SieveAccountCapability(@Nullable String implementation) implements AccountCapability {}
