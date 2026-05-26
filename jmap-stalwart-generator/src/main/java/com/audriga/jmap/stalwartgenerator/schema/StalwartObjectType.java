package com.audriga.jmap.stalwartgenerator.schema;

import com.audriga.jmap.stalwartgenerator.gson.RenameTag;
import com.google.common.base.CaseFormat;

@RenameTag(CaseFormat.LOWER_CAMEL)
public sealed interface StalwartObjectType {
    record Singleton(String description, String permissionPrefix, boolean enterprise) implements StalwartObjectType {}

    record Object(String description, String permissionPrefix, boolean enterprise) implements StalwartObjectType {}

    record View(String objectName) implements StalwartObjectType {}
}
