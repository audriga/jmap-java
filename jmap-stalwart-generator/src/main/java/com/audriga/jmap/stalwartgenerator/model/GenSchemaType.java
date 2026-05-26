package com.audriga.jmap.stalwartgenerator.model;

public sealed interface GenSchemaType extends GenClass permits GenStruct, GenSealed {}
