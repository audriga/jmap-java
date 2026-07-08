package com.audriga.jmap.contacts.entity;

import java.net.URI;
import org.jspecify.annotations.Nullable;

public interface Resource {
    @Nullable
    String kind();

    URI uri();

    @Nullable
    String mediaType();

    @Nullable
    Contexts contexts();

    @Nullable
    Integer pref();

    @Nullable
    String label();
}
