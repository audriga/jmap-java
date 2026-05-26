package rs.ltt.jmap.gson.adapter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import rs.ltt.jmap.gson.AtTypeSealedAdapterFactory;
import rs.ltt.jmap.gson.TagRepr;

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
        var variant = source.variantOf(value);

        out.beginObject();
        // no pattern matching switch in Java 17 yet
        if (tagRepr instanceof TagRepr.External) {
            out.name(variant.tag());
            variant.adapter().write(out, value);
        } else if (tagRepr instanceof TagRepr.Internal internal) {
            var object = variant.adapter().toJsonTree(value).getAsJsonObject();
            if (object.has(internal.property())) {
                throw new JsonParseException("cannot serialize "
                        + value
                        + " as sum type because its serialized form "
                        + object
                        + " already contains the property " + internal.property());
            }
            out.name(internal.property());
            out.value(variant.tag());
            for (var entry : object.entrySet()) {
                out.name(entry.getKey());
                jsonElementAdapter.write(out, entry.getValue());
            }
        } else throw new AssertionError();
        out.endObject();
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (tagRepr instanceof TagRepr.External) {
            in.beginObject();
            var tag = in.nextName();
            var value = source.adapterFor(tag).read(in);
            in.endObject();
            return value;
        } else if (tagRepr instanceof TagRepr.Internal internal) {
            var object = jsonElementAdapter.read(in).getAsJsonObject();
            String tag;
            if (object.has(internal.property())) {
                tag = object.remove(internal.property()).getAsString();
            } else if (internal.defaultTag() != null) {
                tag = internal.defaultTag();
            } else {
                throw new JsonParseException("missing tag property '" + internal.property() + "' for " + object);
            }
            return source.adapterFor(tag).fromJsonTree(object);
        } else throw new AssertionError();
    }
}
