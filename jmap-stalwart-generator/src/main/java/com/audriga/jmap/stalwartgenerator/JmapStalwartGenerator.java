package com.audriga.jmap.stalwartgenerator;

import com.audriga.jmap.stalwartgenerator.gson.SealedTypeAdapterFactory;
import com.audriga.jmap.stalwartgenerator.schema.StalwartSchema;
import com.google.common.io.MoreFiles;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.palantir.javapoet.*;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Objects;

public final class JmapStalwartGenerator {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapterFactory(new SealedTypeAdapterFactory())
            .create();
    private static final Object BUNDLED_LOCK = new Object();
    private static volatile StalwartSchema bundled;

    public static StalwartSchema parseSchema(Reader input) {
        return GSON.fromJson(input, StalwartSchema.class);
    }

    public static StalwartSchema bundledSchema() {
        if (bundled == null) {
            synchronized (BUNDLED_LOCK) {
                if (bundled == null) {
                    try (var stream = JmapStalwartGenerator.class.getResourceAsStream("/schema.json")) {
                        bundled = parseSchema(new InputStreamReader(Objects.requireNonNull(stream)));
                    } catch (Exception e) {
                        throw new IllegalStateException("failed to read bundled schema", e);
                    }
                }
            }
        }
        return bundled;
    }

    public static void generate(Config config, StalwartSchema schema) throws IOException {
        if (config.overwrite() && Files.exists(config.baseDir())) {
            // delete everything first for a clean slate without old leftovers
            MoreFiles.deleteRecursively(config.baseDir());
        }
        Files.createDirectories(config.baseDir());

        Template.BUNDLED.apply(
                Map.of(
                        "pkg", config.pkg(),
                        "pkgPath", config.pkgPath()),
                config.baseDir());

        var ctx = new Context(config.pkg());

        ctx.toModel(schema).forEach(classModel -> {
            var type = classModel
                    .generate(ctx)
                    .alwaysQualify("Get", "Set", "Query")
                    .build();
            try {
                JavaFile.builder(ctx.pkg(), type).build().writeTo(config.baseDir());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    public static AnnotationSpec serializedName(String value) {
        return AnnotationSpec.builder(SerializedName.class)
                .addMember("value", "$S", value)
                .build();
    }
}
