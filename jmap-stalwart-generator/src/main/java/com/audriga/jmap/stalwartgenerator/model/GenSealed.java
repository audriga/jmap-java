package com.audriga.jmap.stalwartgenerator.model;

import static com.google.common.html.HtmlEscapers.htmlEscaper;

import com.audriga.jmap.annotation.Immutable;
import com.audriga.jmap.annotation.Inline;
import com.audriga.jmap.annotation.ServerSet;
import com.audriga.jmap.annotation.Type;
import com.audriga.jmap.stalwartgenerator.Context;
import com.audriga.jmap.stalwartgenerator.Types;
import com.palantir.javapoet.*;
import java.util.stream.Stream;
import javax.lang.model.element.Modifier;
import org.jspecify.annotations.Nullable;

public record GenSealed(
        String schemaName,
        String javaName,
        Stream<Variant> variants,
        @Nullable EntityInfo entityInfo) implements GenSchemaType {
    public record Variant(
            String schemaName,
            String javaName,
            String label,
            @Nullable ClassName innerType) {}

    @Override
    public TypeSpec.Builder generate(Context ctx) {
        var variantList = variants.toList();

        // Fall back to the first variant if the server omits the "@type" discriminator,
        // e.g. in a Foo/set create response.
        var typeAnnotation = AnnotationSpec.builder(Type.class);
        if (!variantList.isEmpty()) {
            typeAnnotation.addMember("value", "$S", variantList.get(0).schemaName);
        }
        var interfaceBuilder = TypeSpec.interfaceBuilder(javaName)
                .addModifiers(Modifier.PUBLIC, Modifier.SEALED)
                .addAnnotation(typeAnnotation.build());
        if (entityInfo != null) {
            entityInfo.apply(ctx, interfaceBuilder, this);
        }
        variantList.forEach(variant -> {
            var variantBuilder = TypeSpec.recordBuilder(variant.javaName)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addSuperinterface(ctx.type(javaName))
                    .addJavadoc("$L", htmlEscaper().escape(variant.label))
                    .addAnnotation(AnnotationSpec.builder(Type.class)
                            .addMember("value", "$S", variant.schemaName)
                            .build());
            var recordCtor = MethodSpec.constructorBuilder();
            if (entityInfo != null) {
                recordCtor.addParameter(ParameterSpec.builder(Types.nullable(Types.STRING), "id")
                        .addAnnotation(Override.class)
                        .addAnnotation(Immutable.class)
                        .addAnnotation(ServerSet.class)
                        .build());
            }
            if (variant.innerType != null) {
                recordCtor.addParameter(ParameterSpec.builder(variant.innerType, "data")
                        .addAnnotation(Inline.class)
                        .build());
            }
            interfaceBuilder.addType(
                    variantBuilder.recordConstructor(recordCtor.build()).build());
        });
        return interfaceBuilder;
    }
}
