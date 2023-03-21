package com.benrhine.plugins.v1.tasks;

import static com.benrhine.plugins.v1.util.ExtensionHelpers.generateVersion;
import static com.benrhine.plugins.v1.util.ExtensionHelpers.getExtensionDefinedRemoteBuild;
import static com.benrhine.plugins.v1.util.ExtensionHelpers.getLocalProperties;
import static com.benrhine.plugins.v1.util.ExtensionHelpers.loadLocalPropertiesToProjectProperties;

import java.io.IOException;
import java.util.Properties;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

/**  --------------------------------------------------------------------------------------------------------------------
 * PrintVersionTask: Print the current version at any point via task.
 * ------------------------------------------------------------------------------------------------------------------ */
public class PrintVersionTask extends DefaultTask {

    /**
     * printVersion: Task that executes printing the version.
     */
    @TaskAction
    public void printVersion() {

        try {
            final Project project = getProject();
            final Properties prop = getLocalProperties(project);
            // Move the locally read properties to the project properties
            loadLocalPropertiesToProjectProperties(project, prop);
            // Determine if this is a remote build (is this a CI build)
            final boolean isRemoteBuild = getExtensionDefinedRemoteBuild(project, prop);
            // Generate the full project version
            generateVersion(project, isRemoteBuild);
            // Print out the complete project version
            System.out.println(project.getProperties().get("version"));
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }
}
