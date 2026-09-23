package com.audriga.jmap.preferences;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapCapability;
import com.audriga.jmap.common.entity.Capability;
import java.util.Map;
import org.jspecify.annotations.Nullable;

@JmapCapability(namespace = Namespace.PREFERENCES)
@lombok.Builder(toBuilder = true)
public record PreferencesCapability(@Nullable Map<String, Object> subCapabilities) implements Capability {}
