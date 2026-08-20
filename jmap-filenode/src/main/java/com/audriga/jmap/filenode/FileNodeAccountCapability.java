package com.audriga.jmap.filenode;

import com.audriga.jmap.Namespace;
import com.audriga.jmap.annotation.JmapAccountCapability;
import com.audriga.jmap.common.entity.AccountCapability;
import java.util.List;
import org.jspecify.annotations.Nullable;

@JmapAccountCapability(namespace = Namespace.FILE_NODE)
@lombok.Builder(toBuilder = true)
public record FileNodeAccountCapability(
        @Nullable Long maxFileNodeDepth,
        long maxSizeFileNodeName,
        @Nullable String forbiddenNameChars,
        @Nullable List<String> forbiddenNodeNames,
        List<String> fileNodeQuerySortOptions,
        boolean mayCreateTopLevelFileNode,
        @Nullable String webTrashUrl,
        boolean caseInsensitiveNames,
        @Nullable String webUrlTemplate,
        @Nullable String webWriteUrlTemplate)
        implements AccountCapability {}
