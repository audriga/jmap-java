package rs.ltt.jmap.calendars.entity;

import java.net.URI;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import rs.ltt.jmap.annotation.Default;
import rs.ltt.jmap.annotation.Type;

@Type
public record VirtualLocation(
        @Default("\"\"") String name, URI uri, @Nullable Set<String> features) {}
