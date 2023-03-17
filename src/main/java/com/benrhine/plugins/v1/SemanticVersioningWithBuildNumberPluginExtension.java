package com.benrhine.plugins.v1;

/** --------------------------------------------------------------------------------------------------------------------
 * SemanticVersioningWithBuildNumberPluginExtension: Extension class in support of plugin configuration.
 * <p>
 * versionConfig {
 *     remoteBuild = true
 *     ciBuildNumberEnvVarName = "BUILD_RUN_NUMBER" //BITBUCKET_BUILD_NUMBER
 *     artifactType = "SNAPSHOT"
 *     includeReleaseTag = true
 *     includeBuildNumber = true
 *     customVersionPropertiesPath = "$projectDir/src/main/resources/version.properties"
 * }
 * ------------------------------------------------------------------------------------------------------------------ */
public class SemanticVersioningWithBuildNumberPluginExtension {

    private boolean remoteBuild = false;
    private boolean includeReleaseTag = false;
    private boolean includeBuildNumber = false;
    private String ciBuildNumberEnvVarName = null;
    private String artifactType = "LOCAL";
    private String customVersionPropertiesPath = "gradle.properties";

    /**
     * isRemoteBuild: Check if the build is performed remotely i.e. is this a CI build.
     *
     * @return boolean
     */
    public boolean isRemoteBuild() {
        return this.remoteBuild;
    }

    /**
     * setRemoteBuild: Set if remote build.
     *
     * @param remoteBuild boolean
     */
    public void setRemoteBuild(final boolean remoteBuild) {
        this.remoteBuild = remoteBuild;
    }

    /**
     * isIncludeReleaseTag: Do you want to include the word RELEASE on your release build.
     *
     * @return boolean
     */
    public boolean isIncludeReleaseTag() {
        return includeReleaseTag;
    }

    /**
     * setIncludeReleaseTag: Set if you want to include the release tag.
     *
     * @param includeReleaseTag boolean
     */
    public void setIncludeReleaseTag(final boolean includeReleaseTag) {
        this.includeReleaseTag = includeReleaseTag;
    }

    /**
     * isIncludeBuildNumber: Do you want to include the build number in your release?
     *
     * @return boolean
     */
    public boolean isIncludeBuildNumber() {
        return includeBuildNumber;
    }

    /**
     * setIncludeBuildNumber: Set if you want to include the build number.
     *
     * @param includeBuildNumber boolean
     */
    public void setIncludeBuildNumber(final boolean includeBuildNumber) {
        this.includeBuildNumber = includeBuildNumber;
    }

    /**
     * getCiBuildNumberEnvVarName: Return the ENV VAR name that was set to attempt to get the build number.
     *
     * @return String
     */
    public String getCiBuildNumberEnvVarName() {
        return this.ciBuildNumberEnvVarName;
    }

    /**
     * setCiBuildNumberEnvVarName: Set the ENV VAR name to your CI/CD predefined variable.
     *
     * @param ciBuildNumberEnvVarName String
     */
    public void setCiBuildNumberEnvVarName(final String ciBuildNumberEnvVarName) {
        this.ciBuildNumberEnvVarName = ciBuildNumberEnvVarName;
    }

    /**
     * getArtifactType: What artifact type is set?
     *
     * @return String
     */
    public String getArtifactType() {
        return artifactType;
    }

    /**
     * setArtifactType: Set the artifact type of the build.
     *
     * @param artifactType String
     */
    public void setArtifactType(final String artifactType) {
        this.artifactType = artifactType.toUpperCase();
    }

    /**
     * getCustomVersionPropertiesPath: Return the custom path to properties file.
     *
     * @return String
     */
    public String getCustomVersionPropertiesPath() {
        return this.customVersionPropertiesPath;
    }

    /**
     * setCustomVersionPropertiesPath: Set if you want to use a non default properties file.
     *
     * @param customVersionPropertiesPath String
     */
    public void setCustomVersionPropertiesPath(final String customVersionPropertiesPath) {
        this.customVersionPropertiesPath = customVersionPropertiesPath;
    }
}
