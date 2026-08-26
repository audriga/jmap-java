package com.audriga.jmap.annotation.processor;

import static com.google.common.base.Throwables.getStackTraceAsString;

import com.audriga.jmap.annotation.RecordBuilder;
import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.common.method.ResultReference;
import com.google.auto.common.MoreElements;
import com.google.auto.service.AutoService;
import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.FieldSpec;
import com.palantir.javapoet.JavaFile;
import com.palantir.javapoet.MethodSpec;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import com.palantir.javapoet.TypeSpec;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

@SupportedAnnotationTypes("com.audriga.jmap.annotation.RecordBuilder")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@AutoService(Processor.class)
public final class RecordBuilderProcessor extends AbstractProcessor {
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (annotations.isEmpty()) return true;

        for (var element : roundEnv.getElementsAnnotatedWith(RecordBuilder.class)) {
            var annotationMirror = MoreElements.getAnnotationMirror(element, RecordBuilder.class)
                    .get();

            if (element.getKind() != ElementKind.RECORD) {
                error("@Builder can only be applied to records", element, annotationMirror);
                continue;
            }

            var typeElem = (TypeElement) element;
            var recordName = ClassName.get(typeElem);
            var builderImplName =
                    ClassName.get(recordName.packageName(), String.join("", recordName.simpleNames()) + "Builder");
            var publicBuilderName = recordName.nestedClass("Builder");
            // Concat all simple names, since we can't generate a class nested inside an existing one,
            // and suffix with Builder.
            var typeSpec = TypeSpec.classBuilder(builderImplName)
                    .addModifiers(Modifier.ABSTRACT, Modifier.SEALED)
                    .addPermittedSubclass(publicBuilderName)
                    .addMethod(MethodSpec.methodBuilder("__this")
                            .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
                            .returns(publicBuilderName)
                            .build());
            var ofMethodSpec = MethodSpec.methodBuilder("of")
                    .addModifiers(Modifier.STATIC)
                    .addParameter(recordName, "value")
                    .returns(publicBuilderName)
                    .addStatement("final var builder = $T.builder()", recordName);
            var buildMethodSpec = MethodSpec.methodBuilder("build")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(recordName);
            var ctorArgs = new StringJoiner(", ");
            for (var component : typeElem.getRecordComponents()) {
                var type = TypeName.get(component.asType());
                var boxed = type.box();
                var name = component.getSimpleName().toString();
                var methodSpec = MethodSpec.methodBuilder(name)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(publicBuilderName)
                        .addParameter(type, "value")
                        .addStatement("this.$L = value", name)
                        .addStatement("return __this()")
                        .build();
                typeSpec.addField(
                                FieldSpec.builder(boxed, name, Modifier.PRIVATE).build())
                        .addMethod(methodSpec);
                // Special case this for now. If more such use cases arise, consider reasonable abstractions
                if (type instanceof ParameterizedTypeName p
                        && p.rawType().equals(ClassName.get(MethodCall.Arg.class))) {
                    for (var innerType : List.of(p.typeArguments().get(0), ClassName.get(ResultReference.class))) {
                        typeSpec.addMethod(MethodSpec.methodBuilder(name)
                                .addModifiers(Modifier.PUBLIC)
                                .returns(publicBuilderName)
                                .addParameter(innerType, "value")
                                .addStatement("$N($T.of(value))", methodSpec, MethodCall.Arg.class)
                                .addStatement("return __this()")
                                .build());
                    }
                }
                ofMethodSpec.addStatement("builder.$N(value.$L())", methodSpec, name);
                if (type.isPrimitive()) {
                    buildMethodSpec.addCode(
                            """
                            if ($L == null) {
                                throw new $T($S);
                            }
                            """, name, IllegalStateException.class, "required field " + name + " was not set");
                }
                ctorArgs.add("this." + name);
            }
            ofMethodSpec.addStatement("return builder");
            buildMethodSpec.addStatement("return new $T($L)", recordName, ctorArgs.toString());
            typeSpec.addMethod(ofMethodSpec.build()).addMethod(buildMethodSpec.build());
            try {
                JavaFile.builder(builderImplName.packageName(), typeSpec.build())
                        .build()
                        .writeTo(processingEnv.getFiler());
            } catch (IOException e) {
                error("failed to write source file for " + builderImplName + ": " + getStackTraceAsString(e));
            }
        }
        return true;
    }

    private void error(String msg) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg);
    }

    private void error(String msg, Element e, AnnotationMirror a) {
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, msg, e, a);
    }
}
