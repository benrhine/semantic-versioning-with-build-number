package com.benrhine.plugins.v1.tasks.base;

import java.util.Properties;
import com.benrhine.plugins.v1.SemanticVersioningWithBuildNumberPluginExtension;
import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.tasks.Internal;

/**  --------------------------------------------------------------------------------------------------------------------
 * VersionTask: This is currently unused, but I have left it here as an alternative example of how it is possible to extract
 * functions when writing a gradle plugin. The tasks could then extend this class instead of DefaultTask.
 * ------------------------------------------------------------------------------------------------------------------ */
public class VersionTask extends DefaultTask {

    @Internal
    protected String getExtensionDefinedPath() {
        final SemanticVersioningWithBuildNumberPluginExtension extension = (SemanticVersioningWithBuildNumberPluginExtension) getProject().getExtensions().findByName("versionConfig");
        String path = "version.properties";
        if (extension != null) {
            final String extensionDefinedPath = extension.getCustomVersionPropertiesPath();

            if (extensionDefinedPath != null) {
                path = extensionDefinedPath;
            }
        }
        return path;
    }

    @Internal
    protected String getExtensionDefinedRemoteBuild(final Properties prop) {
        final SemanticVersioningWithBuildNumberPluginExtension extension = (SemanticVersioningWithBuildNumberPluginExtension) getProject().getExtensions().findByName("versionConfig");
        String isRemoteBuild = prop.getProperty("remote-build");
        if (extension != null) {
            final boolean extensionDefinedRemoteBuild = extension.isRemoteBuild();

            if (extensionDefinedRemoteBuild) {
                isRemoteBuild = String.valueOf(extensionDefinedRemoteBuild);
            }
        }
        return isRemoteBuild;
    }

    @Internal
    protected String getExtensionDefinedArtifactType(final Properties prop) {
        final SemanticVersioningWithBuildNumberPluginExtension extension = (SemanticVersioningWithBuildNumberPluginExtension) getProject().getExtensions().findByName("versionConfig");
        String path = prop.getProperty("artifact-type");
        if (extension != null) {
            final String extensionDefinedArtifactType = extension.getArtifactType();

            if (extensionDefinedArtifactType != null) {
                path = extensionDefinedArtifactType;
            }
        }
        return path;
    }

    @Internal
    protected String generateVersionWithArtifactType(final Project project) {
        final SemanticVersioningWithBuildNumberPluginExtension extension = (SemanticVersioningWithBuildNumberPluginExtension) getProject().getExtensions().findByName("versionConfig");
        final String artifactType = extension.getArtifactType();

        // Check if a custom artifact type is being specified through the extension
        if (artifactType != null && !artifactType.isEmpty()) {
            project.setProperty("artifact-type", artifactType);
            // If one is specified through the extension, use that instead of the default value from version.properties
            return project.getProperties().get("major") + "." + project.getProperties().get("minor") + "." +
                    project.getProperties().get("patch") + "-" + artifactType;
        } else {
            // If no artifact type is specified through the extension, use the default value from version.properties
            return project.getProperties().get("major") + "." + project.getProperties().get("minor") + "." +
                    project.getProperties().get("patch") + "-" + project.getProperties().get("artifact-type");
        }
    }

    @Internal
    protected String generateVersionWithBuildNumberAndArtifactType(final Project project) {
        final SemanticVersioningWithBuildNumberPluginExtension extension = (SemanticVersioningWithBuildNumberPluginExtension) getProject().getExtensions().findByName("versionConfig");
        final String ciBuildNumberEnvVarName = extension.getCiBuildNumberEnvVarName();
        final String artifactType = extension.getArtifactType();
        String buildNumber;
        // Check that the ENV VAR for the build number is passed in and is not null or empty
        if (ciBuildNumberEnvVarName != null && !ciBuildNumberEnvVarName.isEmpty()) {
            // Check if it matches known CI build number environment vars
            if (ciBuildNumberEnvVarName.equalsIgnoreCase("BUILD_RUN_NUMBER") ||
                    ciBuildNumberEnvVarName.equalsIgnoreCase("BITBUCKET_BUILD_NUMBER")) {
                // Attempt to get the build number
                System.out.println("Provided ENV VAR name matches predefined GitHub or BitBucket build number variable");
                buildNumber = System.getenv(extension.getCiBuildNumberEnvVarName());
            } else {
                // Even if it doesn't match known build environment vars, try to get it anyway.
                System.out.println("Unknown ENV VAR name - This may have unexpected results");
                buildNumber = System.getenv(extension.getCiBuildNumberEnvVarName());
            }
            // Check if the build number is null, if it is throw an exception
            if (buildNumber == null) {
                throw new RuntimeException("Provided ENV VAR for build number returned null value - Unable to build version that includes build number");
            }

            // Check if a custom artifact type is being specified through the extension
            if (artifactType != null) {
                // If one is specified through the extension, use that instead of the default value from version.properties
                return project.getProperties().get("major") + "." + project.getProperties().get("minor") + "." +
                        project.getProperties().get("patch") + buildNumber + "-" + artifactType;
            } else {
                // If no artifact type is specified through the extension, use the default value from version.properties
                return project.getProperties().get("major") + "." + project.getProperties().get("minor") + "." +
                        project.getProperties().get("patch") + buildNumber + "-" + project.getProperties().get("artifact-type");
            }
        } else {
            return this.generateVersionWithArtifactType(project);
        }
    }
}
