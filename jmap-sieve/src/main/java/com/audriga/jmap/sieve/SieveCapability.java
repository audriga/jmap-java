package com.audriga.jmap.sieve;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapCapability;
import com.audriga.jmap.common.entity.Capability;

@JmapCapability(namespace = Namespace.SIEVE)
public record SieveCapability() implements Capability {}
