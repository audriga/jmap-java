package com.audriga.jmap.stalwartgenerator.model;

import static com.google.common.html.HtmlEscapers.htmlEscaper;

import com.audriga.jmap.annotation.JmapEntity;
import com.audriga.jmap.annotation.JmapMethod;
import com.audriga.jmap.common.entity.Identifiable;
import com.audriga.jmap.common.method.MethodCall;
import com.audriga.jmap.common.method.MethodResponse;
import com.audriga.jmap.common.method.call.singleton.SingletonGetMethodCall;
import com.audriga.jmap.common.method.call.singleton.SingletonSetMethodCall;
import com.audriga.jmap.common.method.call.standard.GetMethodCall;
import com.audriga.jmap.common.method.call.standard.QueryMethodCall;
import com.audriga.jmap.common.method.call.standard.SetMethodCall;
import com.audriga.jmap.common.method.response.standard.GetMethodResponse;
import com.audriga.jmap.common.method.response.standard.QueryMethodResponse;
import com.audriga.jmap.common.method.response.standard.SetMethodResponse;
import com.audriga.jmap.stalwartgenerator.Context;
import com.palantir.javapoet.*;
import java.util.Arrays;
import java.util.List;
import javax.lang.model.element.Modifier;

public record EntityInfo(String description, String permissionPrefix, boolean singleton, boolean enterprise) {
    public void apply(Context ctx, TypeSpec.Builder builder, GenSchemaType schemaType) {
        var selfType = ClassName.get(ctx.pkg(), schemaType.javaName());
        builder.addSuperinterface(Identifiable.class)
                .addAnnotation(AnnotationSpec.builder(JmapEntity.class)
                        .addMember("name", "$S", schemaType.schemaName())
                        .build())
                .addJavadoc(
                        """
                                $L
                                <p>
                                permission prefix: $L
                                """, htmlEscaper().escape(description), htmlEscaper().escape(permissionPrefix));
        if (enterprise) {
            builder.addJavadoc("<br>enterprise: true");
        }
        if (singleton) {
            builder.addType(Method.SINGLETON_GET
                            .generateImpl(selfType, schemaType.schemaName())
                            .addMethod(
                                    recordCtorImplementing(Method.GET.callInterface, selfType, Method.GET.callMethods)
                                            .addCode("""
                                            this(accountId, properties);
                                            if (!IDS.equals(ids)) {
                                                throw new $T("expected [\\"singleton\\"] ids, got " + ids);
                                            }
                                            """, IllegalArgumentException.class)
                                            .build())
                            .build())
                    .addType(Method.SINGLETON_SET
                            .generateImpl(selfType, schemaType.schemaName())
                            .addMethod(
                                    recordCtorImplementing(Method.SET.callInterface, selfType, Method.SET.callMethods)
                                            .addCode("""
                                            this(accountId, ifInState, $T.asUpdateSingle(update));
                                            if (create != null || destroy != null) {
                                                throw new $T("cannot create or update singleton data type");
                                            }
                                            """, SingletonSetMethodCall.class, IllegalArgumentException.class)
                                            .build())
                            .build());

        } else {
            for (var method : List.of(Method.GET, Method.SET, Method.QUERY)) {
                builder.addType(
                        method.generateImpl(selfType, schemaType.schemaName()).build());
            }
        }
    }

    private enum Method {
        GET(
                "get",
                GetMethodCall.class,
                List.of("accountId", "ids", "properties"),
                GetMethodResponse.class,
                List.of("accountId", "state", "list", "notFound")),
        SET(
                "set",
                SetMethodCall.class,
                List.of("accountId", "ifInState", "create", "update", "destroy"),
                SetMethodResponse.class,
                List.of(
                        "accountId",
                        "oldState",
                        "newState",
                        "created",
                        "updated",
                        "destroyed",
                        "notCreated",
                        "notUpdated",
                        "notDestroyed")),
        QUERY(
                "query",
                QueryMethodCall.class,
                List.of("accountId", "filter", "sort", "position", "anchor", "anchorOffset", "limit", "calculateTotal"),
                QueryMethodResponse.class,
                List.of("accountId", "queryState", "canCalculateChanges", "position", "ids", "total", "limit")),
        SINGLETON_GET(
                "get",
                SingletonGetMethodCall.class,
                List.of("accountId", "properties"),
                GetMethodResponse.class,
                Method.GET.responseMethods),
        SINGLETON_SET(
                "set",
                SingletonSetMethodCall.class,
                List.of("accountId", "ifInState", "updateSingle"),
                SetMethodResponse.class,
                Method.SET.responseMethods);

        private final Class<? extends MethodCall> callInterface;
        private final List<String> callMethods;
        private final Class<? extends MethodResponse> responseInterface;
        private final List<String> responseMethods;
        private final String name;
        private final String className;

        Method(
                String name,
                Class<? extends MethodCall> callInterface,
                List<String> callMethods,
                Class<? extends MethodResponse> responseInterface,
                List<String> responseMethods) {
            this.callInterface = callInterface;
            this.callMethods = callMethods;
            this.responseInterface = responseInterface;
            this.responseMethods = responseMethods;
            this.name = name;
            this.className = Character.toUpperCase(this.name.charAt(0)) + this.name.substring(1);
        }

        private String name(String entity) {
            return "%s/%s".formatted(entity, name);
        }

        public TypeSpec.Builder generateImpl(ClassName entityClass, String entityName) {
            var self = entityClass.nestedClass(className);
            var annotation = AnnotationSpec.builder(JmapMethod.class)
                    .addMember("value", "$S", name(entityName))
                    .build();
            return GenStruct.recordBuilder(TypeSpec.recordBuilder(className), self)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .addAnnotation(annotation)
                    .recordConstructor(recordCtorImplementing(callInterface, entityClass, callMethods)
                            .build())
                    .addSuperinterface(ParameterizedTypeName.get(ClassName.get(callInterface), entityClass))
                    .addType(GenStruct.recordBuilder(TypeSpec.recordBuilder("Response"), self.nestedClass("Response"))
                            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                            .addAnnotation(annotation)
                            .recordConstructor(recordCtorImplementing(responseInterface, entityClass, responseMethods)
                                    .build())
                            .addSuperinterface(responseInterface)
                            .build());
        }
    }

    private static MethodSpec.Builder recordCtorImplementing(Class<?> iface, TypeName param, List<String> methods) {
        var params = methods.stream()
                .map(name -> {
                    try {
                        return iface.getMethod(name);
                    } catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(m -> ParameterSpec.builder(
                                substituteT(TypeName.get(m.getGenericReturnType()), param)
                                        .annotated(Arrays.stream(m.getAnnotatedReturnType()
                                                        .getAnnotations())
                                                .map(AnnotationSpec::get)
                                                .toList()),
                                m.getName())
                        .addAnnotations(Arrays.stream(m.getAnnotations())
                                .map(AnnotationSpec::get)
                                .toList())
                        .build())
                .toList();
        return MethodSpec.constructorBuilder().addModifiers(Modifier.PUBLIC).addParameters(params);
    }

    private static TypeName substituteT(TypeName generic, TypeName sub) {
        return switch (generic) {
            case TypeVariableName v -> v.name().equals("T") ? sub : v;
            case ArrayTypeName a ->
                ArrayTypeName.of(substituteT(a.componentType(), sub)).annotated(a.annotations());
            case ParameterizedTypeName p -> {
                var params =
                        p.typeArguments().stream().map(a -> substituteT(a, sub)).toList();
                if (p.enclosingType() != null) {
                    var enclosing = (ParameterizedTypeName) substituteT(p.enclosingType(), sub);
                    yield enclosing
                            .nestedClass(p.rawType().simpleName(), params)
                            .annotated(p.annotations());
                } else {
                    yield ParameterizedTypeName.get(p.rawType(), params.toArray(TypeName[]::new))
                            .annotated(p.annotations());
                }
            }
            default -> generic;
        };
    }
}
