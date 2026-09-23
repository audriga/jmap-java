package com.audriga.jmap.stalwart;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapAccountCapability;
import com.audriga.jmap.common.entity.AccountCapability;

@JmapAccountCapability(namespace = Namespace.STALWART)
public record StalwartJmapAccountCapability() implements AccountCapability {}
