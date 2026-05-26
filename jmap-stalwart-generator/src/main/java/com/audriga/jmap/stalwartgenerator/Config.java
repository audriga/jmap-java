package com.audriga.jmap.stalwartgenerator;

import java.nio.file.Path;

public record Config(Path baseDir, boolean overwrite, String pkg) {
    public String pkgPath() {
        return pkg.replace('.', '/');
    }
}
