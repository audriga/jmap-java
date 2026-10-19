package com.audriga.jmap.tasks;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapAccountCapability;
import com.audriga.jmap.common.entity.AccountCapability;

@JmapAccountCapability(namespace = Namespace.TASKS)
public record TasksAccountCapability() implements AccountCapability {}
