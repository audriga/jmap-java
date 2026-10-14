package com.audriga.jmap.notes;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapCapability;
import com.audriga.jmap.common.entity.Capability;

@JmapCapability(namespace = Namespace.NOTES)
public record NotesCapability() implements Capability {}
