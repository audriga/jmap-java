package com.audriga.jmap.gson.adapter;

import com.audriga.jmap.gson.AtTypeSealedAdapterFactory;
import com.audriga.jmap.gson.TagRepr;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.jspecify.annotations.Nullable;

/**
 * A reusable type adapter for sum types, also referred to as tagged unions.
 *
 * <p>This class only handles reading/writing of the tag plus data.
 * The logic of how to go from value to tag/adapter and from tag to adapter is left up to a {@link VariantSource}.
 * Within the official JMAP specs, and therefore within these core libraries, only internal tagging with the {@code @type} property are used.
 *
 * @param <T> base type to (de)serialize
 * @see AtTypeSealedAdapterFactory
 */
public final class SumTypeAdapter<T> extends TypeAdapter<T> {
    public record Variant<T>(String tag, TypeAdapter<T> adapter) {}

    public interface VariantSource<T> {
        Variant<T> variantOf(T value);

        TypeAdapter<T> adapterFor(String tag) throws JsonParseException;
    }

    public static final class SealedClassSource<T> implements VariantSource<T> {
        private final Class<? super T> base;
        private final Map<Class<?>, Variant<T>> variants;
        private final Map<String, TypeAdapter<T>> adapters;
        private final VariantSource<T> fallback;

        private final class ErrorFallback implements VariantSource<T> {
            @Override
            public Variant<T> variantOf(T value) {
                throw new IllegalArgumentException("unexpected subtype " + value.getClass() + " of " + base.getName());
            }

            @Override
            public TypeAdapter<T> adapterFor(String tag) throws JsonParseException {
                throw new JsonParseException("invalid type tag '" + tag + "' for sealed class " + base.getName());
            }
        }

        private SealedClassSource(
                Class<? super T> base,
                Map<Class<?>, Variant<T>> variants,
                Map<String, TypeAdapter<T>> adapters,
                @Nullable VariantSource<T> fallback) {
            this.base = base;
            this.variants = Map.copyOf(variants);
            this.adapters = Map.copyOf(adapters);
            this.fallback = fallback != null ? fallback : new ErrorFallback();
        }

        /**
         * Returns an immutable set of all the tags recognized by this source.
         *
         * @return an immutable set of valid tags
         */
        public Set<String> tags() {
            return adapters.keySet();
        }

        @Override
        public SumTypeAdapter.Variant<T> variantOf(T value) {
            var res = variants.get(value.getClass());
            if (res == null) {
                return fallback.variantOf(value);
            }
            return res;
        }

        @Override
        public TypeAdapter<T> adapterFor(String tag) throws JsonParseException {
            var res = adapters.get(tag);
            if (res == null) {
                return fallback.adapterFor(tag);
            }
            return res;
        }
    }

    /**
     * Attempts to build a {@link SealedClassSource} from a base class and a variant-generating function.
     * If {@code base} is not sealed, has no permitted subclasses or has at least one non-final subclass, {@code null} is returned.
     * The intended use of this method is within a {@link TypeAdapterFactory}, where {@code null} should be passed on upwards to indicate a type not targeted by the respective factory.
     *
     * @param base        the base sealed class/interface; the generic variance is there to match {@link TypeToken#getRawType()}
     * @param makeVariant a function which is called for every subclass to generate its variant; may throw {@link IllegalArgumentException} if the subclass is invalid, or return {@code null} to skip the subclass
     * @return a {@link SealedClassSource} for the given base type, or {@code null} if the type is not targeted
     * @throws IllegalArgumentException if the base type is targeted, but invalid
     */
    public static <T> @Nullable SealedClassSource<T> sealedClassSource(
            Class<? super T> base,
            Function<Class<?>, @Nullable Variant<T>> makeVariant,
            @Nullable VariantSource<T> fallback) {
        var subclasses = base.getPermittedSubclasses();
        // not a sealed class, invalid for this adapter
        if (subclasses == null) return null;
        // we want at least one subclass
        if (subclasses.length == 0) return null;

        for (var sub : subclasses) {
            // open subclass, reject for now to avoid weirdness
            if (!Modifier.isFinal(sub.getModifiers())) return null;
        }

        var variants = new HashMap<Class<?>, Variant<T>>();
        var adapters = new HashMap<String, TypeAdapter<T>>();
        for (var sub : subclasses) {
            var variant = makeVariant.apply(sub);
            if (variant == null) continue;
            if (adapters.put(variant.tag, variant.adapter) != null) {
                var other = variants.entrySet().stream()
                        .filter(e -> e.getValue().tag().equals(variant.tag))
                        .findAny()
                        .orElseThrow()
                        .getKey();
                throw new IllegalArgumentException(
                        "duplicate tag '" + variant.tag + "' for classes " + other + " and " + sub);
            }
            variants.put(sub, variant);
        }
        return new SealedClassSource<>(base, variants, adapters, fallback);
    }

    private final VariantSource<T> source;
    private final TagRepr tagRepr;
    private final TypeAdapter<JsonElement> jsonElementAdapter;

    public SumTypeAdapter(VariantSource<T> source, TagRepr tagRepr, TypeAdapter<JsonElement> jsonElementAdapter) {
        this.source = source;
        this.tagRepr = tagRepr;
        this.jsonElementAdapter = jsonElementAdapter;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        var variant = source.variantOf(value);

        out.beginObject();
        // no pattern matching switch in Java 17 yet
        if (tagRepr instanceof TagRepr.External) {
            out.name(variant.tag());
            variant.adapter().write(out, value);
        } else if (tagRepr instanceof TagRepr.Internal internal) {
            var object = variant.adapter().toJsonTree(value).getAsJsonObject();
            var existing = object.get(internal.property());
            // accept already present tag if it matches ours, fail otherwise
            if (existing != null
                    && !(existing instanceof JsonPrimitive primitive
                            && primitive.isString()
                            && primitive.getAsString().equals(variant.tag()))) {
                throw new JsonParseException("cannot serialize " + value + " as sum type because its serialized form "
                        + object + " contains conflicting tag property " + internal.property() + " with value "
                        + existing);
            }
            // write tag manually to make sure it comes first
            out.name(internal.property());
            out.value(variant.tag());
            for (var entry : object.entrySet()) {
                // don't serialize tag again
                if (!entry.getKey().equals(internal.property())) {
                    out.name(entry.getKey());
                    jsonElementAdapter.write(out, entry.getValue());
                }
            }
        } else throw new AssertionError();
        out.endObject();
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }

        if (tagRepr instanceof TagRepr.External) {
            in.beginObject();
            var tag = in.nextName();
            var value = source.adapterFor(tag).read(in);
            in.endObject();
            return value;
        } else if (tagRepr instanceof TagRepr.Internal internal) {
            var object = jsonElementAdapter.read(in).getAsJsonObject();
            String tag = Optional.ofNullable(object.get(internal.property()))
                    .map(JsonElement::getAsString)
                    .or(() -> Optional.ofNullable(internal.defaultTag()))
                    .orElseThrow(() ->
                            new JsonParseException("missing tag property '" + internal.property() + "' for " + object));
            return source.adapterFor(tag).fromJsonTree(object);
        } else throw new AssertionError();
    }
}
