package com.audriga.jmap.notes;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapAccountCapability;
import com.audriga.jmap.common.entity.AccountCapability;

@JmapAccountCapability(namespace = Namespace.NOTES)
public record NotesAccountCapability() implements AccountCapability {}
