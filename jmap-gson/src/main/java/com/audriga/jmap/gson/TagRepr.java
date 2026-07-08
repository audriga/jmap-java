package com.audriga.jmap.gson;

import org.jspecify.annotations.Nullable;

public sealed interface TagRepr {
    record External() implements TagRepr {}

    record Internal(String property, @Nullable String defaultTag) implements TagRepr {}
}
