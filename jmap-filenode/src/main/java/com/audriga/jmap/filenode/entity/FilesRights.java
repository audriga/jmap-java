package com.audriga.jmap.filenode.entity;

@lombok.Builder(toBuilder = true)
public record FilesRights(
        boolean mayRead,
        boolean mayAddChildren,
        boolean mayRename,
        boolean mayDelete,
        boolean mayModifyContent,
        boolean mayShare) {}
