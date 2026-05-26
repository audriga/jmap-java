package rs.ltt.jmap.gson;

import static java.lang.invoke.MethodType.methodType;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Arrays;
import rs.ltt.jmap.annotation.Type;
import rs.ltt.jmap.gson.adapter.SumTypeAdapter;

public final class AtTypeSealedAdapterFactory implements TypeAdapterFactory {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        var raw = type.getRawType();
        var baseAnnotation = raw.getAnnotation(Type.class);
        if (baseAnnotation == null) return null;

        var permitted = raw.getPermittedSubclasses();
        if (permitted == null) return null;
        var dynamicClasses = Arrays.stream(permitted)
                .filter(Type.Dynamic.class::isAssignableFrom)
                .toList();
        if (dynamicClasses.size() > 1) {
            throw new IllegalArgumentException("expected at most one dynamic variant class, found " + dynamicClasses);
        }
        @SuppressWarnings("unchecked")
        var fallback = dynamicClasses.isEmpty()
                ? null
                : (SumTypeAdapter.VariantSource<T>) dynamicSource(dynamicClasses.get(0), gson);
        var source = SumTypeAdapter.sealedClassSource(
                raw,
                sub -> {
                    if (Type.Dynamic.class.isAssignableFrom(sub)) return null;
                    var annotation = sub.getAnnotation(Type.class);
                    if (annotation == null) {
                        throw new IllegalArgumentException(
                                "missing @Type annotation on " + sub.getName() + ", a subtype of " + raw.getName());
                    }
                    var tag = annotation.value().isEmpty() ? sub.getSimpleName() : annotation.value();
                    if (tag.isBlank()) {
                        throw new IllegalArgumentException("found blank @Type value on " + sub);
                    }
                    @SuppressWarnings("unchecked")
                    var adapter = (TypeAdapter<T>) gson.getAdapter(sub);
                    return new SumTypeAdapter.Variant<>(tag, adapter);
                },
                fallback);
        if (source == null) return null;

        String defaultTag = null;
        if (!baseAnnotation.value().isEmpty()) {
            defaultTag = baseAnnotation.value();
            if (!source.tags().contains(defaultTag)) {
                throw new IllegalArgumentException(
                        "invalid @Type default value '" + defaultTag + "'" + " on type " + raw.getName());
            }
        }
        return new SumTypeAdapter<>(
                source, new TagRepr.Internal("@type", defaultTag), gson.getAdapter(JsonElement.class));
    }

    private SumTypeAdapter.DynamicSource<Type.Dynamic<Object>, Object> dynamicSource(Class<?> clazz, Gson gson) {
        Method dataMethod;
        try {
            dataMethod = clazz.getMethod("data");
        } catch (NoSuchMethodException e) {
            // clazz extends Type.Dynamic
            throw new AssertionError(e);
        }
        MethodHandle ctor;
        try {
            ctor = LOOKUP.findConstructor(clazz, methodType(void.class, String.class, dataMethod.getReturnType()));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalArgumentException("failed to get dynamic variant constructor", e);
        }
        @SuppressWarnings("unchecked")
        var source = new SumTypeAdapter.DynamicSource<>(
                (tag, data) -> {
                    try {
                        return (Type.Dynamic<Object>) ctor.invoke(tag, data);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                },
                Type.Dynamic::type,
                Type.Dynamic::data,
                (TypeAdapter<Object>) gson.getAdapter(TypeToken.get(dataMethod.getGenericReturnType())));
        return source;
    }
}
