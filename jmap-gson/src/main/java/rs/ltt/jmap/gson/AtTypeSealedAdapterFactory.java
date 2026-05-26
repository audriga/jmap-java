package rs.ltt.jmap.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.DefaultType;
import rs.ltt.jmap.annotation.Type;
import rs.ltt.jmap.gson.adapter.SumTypeAdapter;

public final class AtTypeSealedAdapterFactory implements TypeAdapterFactory {
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        var raw = type.getRawType();
        var subclasses = permittedSubclasses(raw);
        if (subclasses == null) return null;

        var variants = new HashMap<Class<?>, SumTypeAdapter.Variant<T>>();
        var adapters = new HashMap<String, TypeAdapter<T>>();
        for (var sub : subclasses) {
            var annotation = sub.getAnnotation(Type.class);
            if (annotation == null) return null;
            var tag = annotation.value().isEmpty() ? sub.getSimpleName() : annotation.value();
            if (tag.isBlank()) {
                throw new IllegalStateException("found blank @Type value on " + sub);
            }
            @SuppressWarnings("unchecked")
            var adapter = (TypeAdapter<T>) gson.getAdapter(sub);
            if (adapters.put(tag, adapter) != null) {
                var other = variants.entrySet().stream()
                        .filter(e -> e.getValue().tag().equals(tag))
                        .findAny()
                        .orElseThrow()
                        .getKey();
                throw new IllegalStateException("duplicate tag '" + tag + "' for classes " + other + " and " + sub);
            }
            variants.put(sub, new SumTypeAdapter.Variant<>(tag, adapter));
        }

        DefaultType defaultTypeAnn = raw.getAnnotation(DefaultType.class);
        String defaultTag = null;
        if (defaultTypeAnn != null) {
            defaultTag = defaultTypeAnn.value();
            if (!adapters.containsKey(defaultTag)) {
                throw new IllegalStateException("invalid @DefaultType value '" + defaultTag + "'");
            }
        }

        return new SumTypeAdapter<>(
                new SumTypeAdapter.VariantSource<>() {
                    @Override
                    public SumTypeAdapter.Variant<T> variantOf(T value) {
                        var res = variants.get(value.getClass());
                        if (res == null) {
                            throw new IllegalStateException(
                                    "unknown subtype " + value.getClass() + " of " + raw.getName());
                        }
                        return res;
                    }

                    @Override
                    public TypeAdapter<T> adapterFor(String tag) throws JsonParseException {
                        var res = adapters.get(tag);
                        if (res == null) {
                            throw new JsonParseException(
                                    "invalid @type tag " + tag + " for sealed class " + raw.getName());
                        }
                        return res;
                    }
                },
                new TagRepr.Internal("@type", defaultTag),
                gson.getAdapter(JsonElement.class));
    }

    @Nullable
    private static Set<Class<?>> permittedSubclasses(Class<?> clazz) {
        var subclasses = clazz.getPermittedSubclasses();
        // not a sealed class, invalid for this adapter
        if (subclasses == null) return null;
        // we want at least one subclass
        if (subclasses.length == 0) return null;

        for (var sub : subclasses) {
            // open subclass, reject for now to avoid weirdness
            if (!Modifier.isFinal(sub.getModifiers())) return null;
        }
        return Set.of(subclasses);
    }
}
