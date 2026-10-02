package com.audriga.jmap.filenode.entity;

import com.audriga.jmap.annotation.Default;
import com.audriga.jmap.annotation.Immutable;
import com.audriga.jmap.annotation.JmapEntity;
import com.audriga.jmap.annotation.ServerSet;
import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.filenode.method.FileNodeFilterCondition;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.Nullable;

// See https://datatracker.ietf.org/doc/draft-ietf-jmap-filenode/
@JmapEntity(filterCondition = FileNodeFilterCondition.class)
@lombok.Builder(toBuilder = true)
public record FileNode(
        @Immutable @ServerSet String id,
        @Nullable String parentId,
        @Immutable String nodeType,
        @Nullable String blobId,
        @Nullable List<String> target,
        @ServerSet @Nullable Long size,
        String name,
        @Nullable String type,
        Instant created,
        @Nullable Instant modified,
        @Nullable Instant accessed,
        @ServerSet Instant changed,
        @Default("false") Boolean executable,
        @Default("true") Boolean isSubscribed,
        @ServerSet FilesRights myRights,
        @Nullable Map<String, FilesRights> shareWith,
        @Nullable String role)
        implements Identifiable {}
