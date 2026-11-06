package com.audriga.jmap.debug;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapCapability;
import com.audriga.jmap.common.entity.Capability;

@JmapCapability(namespace = Namespace.DEBUG)
public record DebugCapability() implements Capability {}
