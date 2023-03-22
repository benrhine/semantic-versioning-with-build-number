package com.benrhine.plugins.v1.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import com.benrhine.plugins.v1.SemanticVersioningWithBuildNumberPluginExtension;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.convert.DefaultListDelimiterHandler;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.gradle.api.Project;

/** --------------------------------------------------------------------------------------------------------------------
 * ExtensionHelpers: Re-usable helper functions.
 * ------------------------------------------------------------------------------------------------------------------ */
public final class ExtensionHelpers {

    /**
     * getLocalProperties:
     *
     * @param project Project
     * @return Properties
     * @throws IOException an Exception
     */
    public static Properties getLocalProperties(final Project project) throws IOException {
        final InputStream input = new FileInputStream((getExtensionDefinedPath(project)));
        final Properties prop = new OrderedProperties();

        // Load the `gradle.properties` file into the plugin
        prop.load(input);

        return prop;
    }
    public static FileBasedConfigurationBuilder<FileBasedConfiguration> apachePropertiesBuilder(final Project project) {
        final Parameters params = new Parameters();
        return new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setFileName(getExtensionDefinedPath(project))
                                .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
    }

    public static Configuration getLocalPropertiesApache(final Project project) throws ConfigurationException {
        Parameters params = new Parameters();
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setFileName(getExtensionDefinedPath(project))
                                .setListDelimiterHandler(new DefaultListDelimiterHandler(',')));
        return builder.getConfiguration();
    }

    /**
     * writeLocalProperties
     *
     * @param project Project
     * @param prop Properties
     *
     * @throws IOException an Exception
     */
    public static void writeLocalProperties(final Project project, final Properties prop) throws IOException {
        final OutputStream output = new FileOutputStream((getExtensionDefinedPath(project)));
        prop.store(output, "Updating .properties - THIS FILE REGENERATED WHEN TASK IS EXECUTED");

        // System.out.println(prop);
    }

    /**
     * loadLocalPropertiesToProjectProperties
     *
     * @param project Project
     *
     * @param prop Properties
     */
    public static void loadLocalPropertiesToProjectProperties(final Project project, final Properties prop) {
        project.setProperty("major", prop.getProperty("major"));
        project.setProperty("minor", prop.getProperty("minor"));
        project.setProperty("patch", prop.getProperty("patch"));
        project.setProperty("artifact-type", prop.getProperty("artifact-type"));
    }

    public static void loadLocalPropertiesToProjectPropertiesApache(final Project project, final Configuration config) {
        project.setProperty("major", config.getProperty("major"));
        project.setProperty("minor", config.getProperty("minor"));
        project.setProperty("patch", config.getProperty("patch"));
        project.setProperty("artifact-type", config.getProperty("artifact-type"));
    }

    /**
     * getExtensionDefinedPath:
     *
     * @param project Project
     *
     * @return String
     */
    public static String getExtensionDefinedPath(final Project project) {
        final SemanticVersioningWithBuildNumberPluginExtension extension = (SemanticVersioningWithBuildNumberPluginExtension) project.getExtensions().findByName("versionConfig");
        String path = "gradle.properties";
        if (extension != null) {
            final String extensionDefinedPath = extension.getCustomVersionPropertiesPath();

            if (extensionDefinedPath != null) {
                path = extensionDefinedPath;
            }
        }
        return path;
    }

    /**
     * getExtensionDefinedRemoteBuild:
     *
     * @param project Project
     * @param prop Properties
     *
     * @return boolean
     */
    public static boolean getExtensionDefinedRemoteBuild(final Project project, final Properties prop) {
        boolean isRemoteBuild = false;

        if (project.hasProperty("remote-build")) {
            final SemanticVersioningWithBuildNumberPluginExtension extension = (SemanticVersioningWithBuildNumberPluginExtension) project.getExtensions().findByName("versionConfig");

            if (extension != null) {
                isRemoteBuild = extension.isRemoteBuild();
            } else {
                final String propIsRemoteBuild = prop.getProperty("remote-build");

                if (propIsRemoteBuild != null && !propIsRemoteBuild.isEmpty()) {
                    try {
                        isRemoteBuild = Boolean.parseBoolean(propIsRemoteBuild);
                    } catch (final Exception e) {
                        e.printStackTrace();
                        System.out.println("Warning: Could not parse value from properties - defaulting remote build to false");
                    }
                }
            }
        }
        return isRemoteBuild;
    }

    public static boolean getExtensionDefinedRemoteBuildApache(final Project project, final Configuration config) {
        boolean isRemoteBuild = false;

        if (project.hasProperty("remote-build")) {
            final SemanticVersioningWithBuildNumberPluginExtension extension = (SemanticVersioningWithBuildNumberPluginExtension) project.getExtensions().findByName("versionConfig");

            if (extension != null) {
                isRemoteBuild = extension.isRemoteBuild();
            } else {
                final String propIsRemoteBuild = config.getProperty("remote-build").toString();

                if (propIsRemoteBuild != null && !propIsRemoteBuild.isEmpty()) {
                    try {
                        isRemoteBuild = Boolean.parseBoolean(propIsRemoteBuild);
                    } catch (final Exception e) {
                        e.printStackTrace();
                        System.out.println("Warning: Could not parse value from properties - defaulting remote build to false");
                    }
                }
            }
        }
        return isRemoteBuild;
    }

    /**
     * getExtensionDefinedArtifactType:
     *
     * @param project Project
     * @param prop Properties
     * @return String
     */
    protected static String getExtensionDefinedArtifactType(final Project project, final Properties prop) {
        final SemanticVersioningWithBuildNumberPluginExtension extension = (SemanticVersioningWithBuildNumberPluginExtension) project.getExtensions().findByName("versionConfig");
        String path = prop.getProperty("artifact-type");
        if (extension != null) {
            final String extensionDefinedArtifactType = extension.getArtifactType();

            if (extensionDefinedArtifactType != null) {
                path = extensionDefinedArtifactType;
            }
        }
        return path;
    }

    /**
     * generateVersion:
     *
     * @param project Project
     *
     * @param isRemoteBuild boolean
     */
    public static void generateVersion(final Project project, final boolean isRemoteBuild) {
        if (isRemoteBuild) {
            project.setProperty("version", generateVersionWithBuildNumberAndArtifactType(project));
        } else {
            project.setProperty("version", generateVersionWithArtifactType(project));
        }
    }

    /**
     * generateVersionWithArtifactType:
     *
     * @param project Project
     *
     * @return String
     */
    public static String generateVersionWithArtifactType(final Project project) {
        final SemanticVersioningWithBuildNumberPluginExtension extension = (SemanticVersioningWithBuildNumberPluginExtension) project.getExtensions().findByName("versionConfig");
        final String artifactType = extension.getArtifactType();
        final boolean includeReleaseTag = extension.isIncludeReleaseTag();
        String version = project.getProperties().get("major") + "." + project.getProperties().get("minor") + "." +
                project.getProperties().get("patch");

        // Check if a custom artifact type is being specified through the extension
        if (artifactType != null && !artifactType.isEmpty()) {
            checkArtifactType(artifactType);
            project.setProperty("artifact-type", artifactType);

            if (artifactType.equalsIgnoreCase("RELEASE")) {
                if (!includeReleaseTag) {
                    return version;
                }
            }
        }
        // If no artifact type is specified through the extension, use the default value from gradle.properties
        return version + "-" + project.getProperties().get("artifact-type");
    }

    /**
     * checkArtifactType:
     *
     * @param artifactType String
     */
    private static void checkArtifactType(final String artifactType) {
        switch (artifactType) {
            case "LOCAL":
            case "SNAPSHOT":
            case "CANARY":
            case "ALPHA":
            case "BETA":
            case "RELEASE":
            case "EXPERIMENTAL":
                //System.out.println("INFO ONLY!!! Inbound artifact type matches supported / expected artifact types");
                break;
            default:
                System.out.println("INFO ONLY!!! Inbound artifact type DOES NOT MATCH supported / expected artifact types");
                System.out.println("This will not affect plugin function, this is only to inform the user they may have misspelled the artifact type or that they are using an unusual type.");
        }
    }

    /**
     * generateVersionWithBuildNumberAndArtifactType:
     *
     * @param project Project
     *
     * @return String
     */
    public static String generateVersionWithBuildNumberAndArtifactType(final Project project) {
        final SemanticVersioningWithBuildNumberPluginExtension extension = (SemanticVersioningWithBuildNumberPluginExtension) project.getExtensions().findByName("versionConfig");
        final String ciBuildNumberEnvVarName = extension.getCiBuildNumberEnvVarName();
        final String artifactType = extension.getArtifactType();
        final boolean includeReleaseTag = extension.isIncludeReleaseTag();
        final boolean includeBuildNumber = extension.isIncludeBuildNumber();
        String version = project.getProperties().get("major") + "." + project.getProperties().get("minor") + "." +
                project.getProperties().get("patch");
        String buildNumber;
        // Check that the ENV VAR for the build number is passed in and is not null or empty
        if (ciBuildNumberEnvVarName != null && !ciBuildNumberEnvVarName.isEmpty()) {
            // Check if it matches known CI build number environment vars
            if (ciBuildNumberEnvVarName.equalsIgnoreCase("BUILD_RUN_NUMBER") ||
                    ciBuildNumberEnvVarName.equalsIgnoreCase("BITBUCKET_BUILD_NUMBER")) {
                // Attempt to get the build number
                System.out.println("Warning: Provided ENV VAR name matches predefined GitHub or BitBucket build number variable");
                buildNumber = System.getenv(extension.getCiBuildNumberEnvVarName());
            } else {
                // Even if it doesn't match known build environment vars, try to get it anyway.
                System.out.println("Warning: Unknown ENV VAR name - This may have unexpected results");
                buildNumber = System.getenv(extension.getCiBuildNumberEnvVarName());
            }
            // Check if the build number is null, if it is throw an exception
//            if (buildNumber == null) {
//                throw new RuntimeException("Provided ENV VAR for build number returned null value - Unable to build version that includes build number");
//            }

            // Check if a custom artifact type is being specified through the extension
            if (artifactType != null && !artifactType.isEmpty()) {
                checkArtifactType(artifactType);
                project.setProperty("artifact-type", artifactType);

                if (artifactType.equalsIgnoreCase("RELEASE")) {
                    if (!includeReleaseTag && !includeBuildNumber) {
                        return version;
                    } else if (includeReleaseTag && !includeBuildNumber) {
                        return version + "-" + artifactType;
                    } else if (!includeReleaseTag) {
                        if (buildNumber != null) {
                            return version + "." + buildNumber;
                        } else {
                            System.out.println("Warning: Build number from ENV VAR was null - VERSION WILL NOT INCLUDE BUILD NUMBER");
                        }
                    }
                }
            }

            if (buildNumber != null) {
                // If no artifact type is specified through the extension, use the default value from gradle.properties
                return version + "." + buildNumber + "-" + project.getProperties().get("artifact-type");
            } else {
                System.out.println("Warning: Build number from ENV VAR was null - VERSION WILL NOT INCLUDE BUILD NUMBER");
                // If no artifact type is specified through the extension, use the default value from gradle.properties
                return version + "-" + project.getProperties().get("artifact-type");
            }
        } else {
            System.out.println("Warning: No ENV VAR for build number has been set | Please add `ciBuildNumberEnvVarName = YOUR-VALUE` to the versionConfig block");
            System.out.println("Warning: Version will NOT set build number for this project run");
            return generateVersionWithArtifactType(project);
        }
    }
}
