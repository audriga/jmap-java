package rs.ltt.jmap.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import rs.ltt.jmap.annotation.Type;
import rs.ltt.jmap.gson.adapter.SumTypeAdapter;

public final class AtTypeSealedAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        var raw = type.getRawType();
        var baseAnnotation = raw.getAnnotation(Type.class);
        if (baseAnnotation == null) return null;
        var source = SumTypeAdapter.sealedClassSource(raw, sub -> {
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
        });
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
}
