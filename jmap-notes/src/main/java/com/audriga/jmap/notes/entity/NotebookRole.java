package com.audriga.jmap.notes.entity;

import com.google.gson.annotations.SerializedName;

public enum NotebookRole {
    @SerializedName("inbox")
    INBOX,
    @SerializedName("trash")
    TRASH,
    @SerializedName("archive")
    ARCHIVE
}
