package com.audriga.jmap.gson;

import com.audriga.jmap.annotation.Inline;
import com.audriga.jmap.annotation.Type;
import com.audriga.jmap.gson.adapter.SumTypeAdapter;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.Arrays;
import lombok.SneakyThrows;

public final class AtTypeSealedAdapterFactory implements TypeAdapterFactory {
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        var raw = type.getRawType();
        var baseAnnotation = raw.getAnnotation(Type.class);
        if (baseAnnotation == null) return null;

        var permitted = raw.getPermittedSubclasses();
        if (permitted == null) return null;
        var unknownClasses = Arrays.stream(permitted)
                .filter(c -> c.isAnnotationPresent(Type.Unknown.class))
                .toList();
        if (unknownClasses.size() > 1) {
            throw new IllegalArgumentException("expected at most one dynamic variant class, found " + unknownClasses);
        }
        @SuppressWarnings("unchecked")
        var fallback = unknownClasses.isEmpty()
                ? null
                : (SumTypeAdapter.VariantSource<T>) unknownSource(unknownClasses.get(0), gson);
        var source = SumTypeAdapter.sealedClassSource(
                raw,
                sub -> {
                    if (sub.isAnnotationPresent(Type.Unknown.class)) return null;
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

    private <T> SumTypeAdapter.VariantSource<T> unknownSource(Class<T> clazz, Gson gson) {
        if (clazz.isAnnotationPresent(Type.class)) {
            throw new IllegalArgumentException("found both @Type.Unknown and @Type annotation on " + clazz.getName());
        }
        if (!clazz.isAnnotationPresent(Inline.class)) {
            throw new IllegalArgumentException("@Type.Unknown class " + clazz.getName() + " must be marked @Inline");
        }
        var components = clazz.getRecordComponents();
        if (components == null) {
            throw new IllegalArgumentException("@Type.Unknown class " + clazz.getName() + " is not a record");
        }
        if (components.length != 1 || components[0].getType() != JsonObject.class) {
            throw new IllegalArgumentException("expected one component of type JsonObject on @Type.Unknown record "
                    + clazz.getName() + ", found " + Arrays.toString(components));
        }
        MethodHandle accessor;
        try {
            accessor = LOOKUP.unreflect(components[0].getAccessor());
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(
                    "cannot access component " + components[0].getName() + " of record " + clazz.getName(), e);
        }
        var adapter = gson.getAdapter(clazz);
        return new SumTypeAdapter.VariantSource<>() {
            @Override
            @SneakyThrows // avoid catching Throwable from accessor
            public SumTypeAdapter.Variant<T> variantOf(T value) {
                var data = (JsonObject) accessor.invoke(value);
                return new SumTypeAdapter.Variant<>(data.get("@type").getAsString(), adapter);
            }

            @Override
            public TypeAdapter<T> adapterFor(String tag) throws JsonParseException {
                return adapter;
            }
        };
    }
}
