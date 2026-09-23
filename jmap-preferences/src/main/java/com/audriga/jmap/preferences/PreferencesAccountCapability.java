package com.audriga.jmap.preferences;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapAccountCapability;
import com.audriga.jmap.common.entity.AccountCapability;

@JmapAccountCapability(namespace = Namespace.PREFERENCES)
public record PreferencesAccountCapability() implements AccountCapability {}
