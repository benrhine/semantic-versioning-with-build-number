package com.benrhine.plugins.v1.tasks;

import static com.benrhine.plugins.v1.util.ExtensionHelpers.generateVersion;
import static com.benrhine.plugins.v1.util.ExtensionHelpers.getExtensionDefinedRemoteBuild;
import static com.benrhine.plugins.v1.util.ExtensionHelpers.getLocalProperties;
import static com.benrhine.plugins.v1.util.ExtensionHelpers.loadLocalPropertiesToProjectProperties;
import static com.benrhine.plugins.v1.util.ExtensionHelpers.writeLocalProperties;

import java.io.IOException;
import java.util.Properties;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

/**  --------------------------------------------------------------------------------------------------------------------
 * MajorVersion: Increment the major version via task.
 * ------------------------------------------------------------------------------------------------------------------ */
public class IncrementMajorVersionTask extends DefaultTask {

    /**
     * incrementMajorVersion: Task that executes incrementing the major version.
     *
     * @throws Exception (optionally)
     */
    @TaskAction
    public void incrementMajorVersion() throws Exception {

        try {
            final Project project = getProject();
            final Properties prop = getLocalProperties(project);
            // Increment the major version and store it into the local properties object
            final int major = Integer.parseInt(prop.getProperty("major"));
            prop.setProperty("major", String.valueOf(major + 1));
            // Move the locally read properties to the project properties
            loadLocalPropertiesToProjectProperties(project, prop);
            // Determine if this is a remote build (is this a CI build)
            final boolean isRemoteBuild = getExtensionDefinedRemoteBuild(project, prop);
            // Generate the full project version
            generateVersion(project, isRemoteBuild);
            // Save `version.properties` to the root project folder OR to the location specified in the
            // `build.gradle` extension block
            writeLocalProperties(project, prop);
            System.out.println(project.getProperties().get("version"));
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

}
