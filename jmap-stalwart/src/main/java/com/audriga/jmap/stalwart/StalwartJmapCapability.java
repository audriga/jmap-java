package com.audriga.jmap.stalwart;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapCapability;
import com.audriga.jmap.common.entity.Capability;

@JmapCapability(namespace = Namespace.STALWART)
public record StalwartJmapCapability() implements Capability {}
