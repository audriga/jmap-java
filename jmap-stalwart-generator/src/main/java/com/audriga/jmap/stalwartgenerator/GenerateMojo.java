package com.audriga.jmap.stalwartgenerator;

import com.audriga.jmap.stalwartgenerator.schema.StalwartSchema;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public final class GenerateMojo extends AbstractMojo {
    @Parameter(defaultValue = "com.audriga.jmap.stalwart")
    private String basePackage;

    @Parameter(defaultValue = "${project.build.directory}/generated-sources/stalwart")
    private Path outputDirectory;

    @Parameter(defaultValue = "true")
    private boolean overwrite;

    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    /**
     * Path to a valid schema.json file.
     * If not given, defaults to the one bundled with the plugin.
     */
    @Parameter
    private Path schemaFile;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        StalwartSchema schema;
        if (schemaFile != null) {
            try {
                schema = JmapStalwartGenerator.parseSchema(Files.newBufferedReader(schemaFile));
            } catch (IOException e) {
                throw new MojoExecutionException("failed to read schema file at " + schemaFile, e);
            }
        } else {
            schema = JmapStalwartGenerator.bundledSchema();
        }
        try {
            JmapStalwartGenerator.generate(new Config(outputDirectory, overwrite, basePackage), schema);
        } catch (IOException e) {
            throw new MojoExecutionException(e);
        }
        project.addCompileSourceRoot(outputDirectory.toString());
    }
}
