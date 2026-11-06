package com.audriga.jmap.debug;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapAccountCapability;
import com.audriga.jmap.common.entity.AccountCapability;

@JmapAccountCapability(namespace = Namespace.DEBUG)
public record DebugAccountCapability() implements AccountCapability {}
