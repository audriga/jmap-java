package com.audriga.jmap.stalwartgenerator.schema;

public record StalwartField(
        String description, StalwartFieldType type, StalwartFieldUpdate update, boolean enterprise) {}
