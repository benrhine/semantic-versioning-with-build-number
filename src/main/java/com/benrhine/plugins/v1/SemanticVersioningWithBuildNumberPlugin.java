package com.benrhine.plugins.v1;

import static com.benrhine.plugins.v1.util.ExtensionHelpers.generateVersion;
import static com.benrhine.plugins.v1.util.ExtensionHelpers.generateVersionWithArtifactType;
import static com.benrhine.plugins.v1.util.ExtensionHelpers.getExtensionDefinedRemoteBuild;
import static com.benrhine.plugins.v1.util.ExtensionHelpers.getLocalProperties;
import static com.benrhine.plugins.v1.util.ExtensionHelpers.loadLocalPropertiesToProjectProperties;
import static com.benrhine.plugins.v1.util.ExtensionHelpers.writeLocalProperties;

import java.io.IOException;
import java.util.Properties;
import com.benrhine.plugins.v1.tasks.DecrementMajorVersionTask;
import com.benrhine.plugins.v1.tasks.DecrementMinorVersionTask;
import com.benrhine.plugins.v1.tasks.DecrementPatchVersionTask;
import com.benrhine.plugins.v1.tasks.IncrementMajorVersionTask;
import com.benrhine.plugins.v1.tasks.IncrementMinorVersionTask;
import com.benrhine.plugins.v1.tasks.IncrementPatchVersionTask;
import com.benrhine.plugins.v1.tasks.PrintVersionTask;
import groovy.lang.Closure;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/** --------------------------------------------------------------------------------------------------------------------
 * SemanticVersioningWithBuildNumberPlugin: Plugin that supports semantic versioning with a build number. Can be used
 * strictly for project versioning or in conjunction with safe agile to match versions to pi and increments.
 * - https://coderanch.com/t/544562/java/save-properties-file-affecting-existing
 * - https://stackoverflow.com/questions/565932/a-better-class-to-update-property-files/565996#565996
 * - https://stackoverflow.com/questions/17418106/appending-data-to-properties-file-comments-disappear-order-of-data-changed
 * - https://javapointers.com/how-to/how-to-load-properties-using-apache-commons-configuration-2/
 * - https://gist.github.com/workspc/a999801492f0ae2cbe07f36e6e256ce8
 * ------------------------------------------------------------------------------------------------------------------ */
public final class SemanticVersioningWithBuildNumberPlugin implements Plugin<Project> {
    static final String VERSION_CONFIG = "versionConfig";
    static final String PRINT_VERSION = "printVersion";
    static final String INCREMENT_MAJOR_VERSION = "incrementMajorVersion";
    static final String INCREMENT_MINOR_VERSION = "incrementMinorVersion";
    static final String INCREMENT_PATCH_VERSION = "incrementPatchVersion";
    static final String DECREMENT_MAJOR_VERSION = "decrementMajorVersion";
    static final String DECREMENT_MINOR_VERSION = "decrementMinorVersion";
    static final String DECREMENT_PATCH_VERSION = "decrementPatchVersion";

    /**
     * apply: Invoke the plugin to be applied on a given project.
     *
     * @param project Project
     */
    @Override
    public void apply(final Project project) {
        // Initialize `build.gradle` extension closure
        final SemanticVersioningWithBuildNumberPluginExtension extension = project.getExtensions().create(VERSION_CONFIG, SemanticVersioningWithBuildNumberPluginExtension.class);
        // Initialize tasks included with the plugin
        project.getTasks().register(INCREMENT_MAJOR_VERSION, IncrementMajorVersionTask.class);
        project.getTasks().register(INCREMENT_MINOR_VERSION, IncrementMinorVersionTask.class);
        project.getTasks().register(INCREMENT_PATCH_VERSION, IncrementPatchVersionTask.class);
        project.getTasks().register(DECREMENT_MAJOR_VERSION, DecrementMajorVersionTask.class);
        project.getTasks().register(DECREMENT_MINOR_VERSION, DecrementMinorVersionTask.class);
        project.getTasks().register(DECREMENT_PATCH_VERSION, DecrementPatchVersionTask.class);
        project.getTasks().register(PRINT_VERSION, PrintVersionTask.class);
        // Apply plugin to project post initialization - without this it will not set the version correctly on initialization
        project.getGradle().afterProject(new Closure<Void>(project) {
            public void doCall(final Project project) {
                try {
                    // Retrieve the `version.properties` file. This can be either at the default location of the project
                    // root OR at a custom path location specified by the `build.gradle` extension block
                    final Properties prop = getLocalProperties(project);
                    // Move the locally read properties to the project properties
                    loadLocalPropertiesToProjectProperties(project, prop);
                    // Determine if this is a remote build (is this a CI build)
                    final boolean isRemoteBuild = getExtensionDefinedRemoteBuild(project, prop);
                    // Generate the full project version
                    generateVersion(project, isRemoteBuild);
                    // artifactType is checked and set to project properties above, make sure it gets set into properties
                    // and stored back to the `version.properties` file.
                    if (project.hasProperty("artifact-type")) {
                        prop.setProperty("artifact-type", project.getProperties().get("artifact-type").toString());
                    }
                    // Save `version.properties` to the root project folder OR to the location specified in the
                    // `build.gradle` extension block
                    writeLocalProperties(project, prop);

                } catch (final IOException ex) {
                    ex.printStackTrace();
                    project.setProperty("version", generateVersionWithArtifactType(project));
                }
            }
        });
    }
}