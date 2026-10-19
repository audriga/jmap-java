package com.audriga.jmap.tasks;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapCapability;
import com.audriga.jmap.common.entity.Capability;

@JmapCapability(namespace = Namespace.TASKS)
public record TasksCapability() implements Capability {}
