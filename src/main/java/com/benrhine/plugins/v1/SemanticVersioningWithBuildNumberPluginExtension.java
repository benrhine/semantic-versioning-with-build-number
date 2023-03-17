package com.benrhine.plugins.v1;

/** --------------------------------------------------------------------------------------------------------------------
 * SemanticVersioningWithBuildNumberPluginExtension: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
public class SemanticVersioningWithBuildNumberPluginExtension {

    private boolean remoteBuild = false;
    private boolean includeReleaseTag = false;
    private boolean includeBuildNumber = false;
    private String ciBuildNumberEnvVarName = null;
    private String artifactType = "LOCAL";
    private String customVersionPropertiesPath = "gradle.properties";

    public boolean isRemoteBuild() {
        return this.remoteBuild;
    }

    public void setRemoteBuild(final boolean remoteBuild) {
        this.remoteBuild = remoteBuild;
    }

    public boolean isIncludeReleaseTag() {
        return includeReleaseTag;
    }

    public void setIncludeReleaseTag(final boolean includeReleaseTag) {
        this.includeReleaseTag = includeReleaseTag;
    }

    public boolean isIncludeBuildNumber() {
        return includeBuildNumber;
    }

    public void setIncludeBuildNumber(final boolean includeBuildNumber) {
        this.includeBuildNumber = includeBuildNumber;
    }

    public String getCiBuildNumberEnvVarName() {
        return this.ciBuildNumberEnvVarName;
    }

    public void setCiBuildNumberEnvVarName(final String ciBuildNumberEnvVarName) {
        this.ciBuildNumberEnvVarName = ciBuildNumberEnvVarName;
    }

    public String getArtifactType() {
        return artifactType;
    }

    public void setArtifactType(final String artifactType) {
        this.artifactType = artifactType.toUpperCase();
    }

    public String getCustomVersionPropertiesPath() {
        return this.customVersionPropertiesPath;
    }

    public void setCustomVersionPropertiesPath(final String customVersionPropertiesPath) {
        this.customVersionPropertiesPath = customVersionPropertiesPath;
    }
}
