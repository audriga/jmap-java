package com.audriga.jmap.filenode;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapCapability;
import com.audriga.jmap.common.entity.Capability;

@JmapCapability(namespace = Namespace.FILE_NODE)
public record FileNodeCapability() implements Capability {}
