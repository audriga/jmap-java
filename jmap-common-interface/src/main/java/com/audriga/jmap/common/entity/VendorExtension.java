package com.audriga.jmap.common.entity;

import java.util.Objects;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

public record VendorExtension(String prefix, String name) {
    public VendorExtension {
        Objects.requireNonNull(prefix);
        Objects.requireNonNull(name);
    }

    // See https://jmap.io/spec/rfc9553/#section-1.8.1
    private static final Pattern PATTERN;

    static {
        var alnumInt = "[a-zA-Z0-9[^\\p{ASCII}]]";
        var vLabel = alnumInt + "(?:(?:" + alnumInt + "|-)*" + alnumInt + ")?";
        PATTERN = Pattern.compile("(?<prefix>" + vLabel + "(?:\\." + vLabel + ")*" + "):(?<name>[^\\p{Cntrl}\"/~]+)");
    }

    @Nullable
    public static VendorExtension parse(String property) {
        var matcher = PATTERN.matcher(property);
        if (!matcher.matches()) return null;
        return new VendorExtension(matcher.group("prefix"), matcher.group("name"));
    }

    @Override
    public String toString() {
        return prefix + ":" + name;
    }
}
