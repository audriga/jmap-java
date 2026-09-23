package com.audriga.jmap.sieve;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapAccountCapability;
import com.audriga.jmap.common.entity.AccountCapability;
import java.util.List;
import org.jspecify.annotations.Nullable;

@JmapAccountCapability(namespace = Namespace.SIEVE)
@lombok.Builder(toBuilder = true)
public record SieveAccountCapability(
        @Nullable String implementation,
        @Nullable Long maxSizeScriptName,
        @Nullable Long maxSizeScript,
        @Nullable Long maxNumberScripts,
        @Nullable Long maxNumberRedirects,
        @Nullable List<String> sieveExtensions,
        @Nullable List<String> notificationMethods,
        @Nullable List<String> externalLists)
        implements AccountCapability {}
