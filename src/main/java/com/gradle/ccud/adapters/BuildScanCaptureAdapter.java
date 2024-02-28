package com.gradle.ccud.adapters;

/**
 * @see com.gradle.develocity.agent.maven.api.scan.BuildScanCaptureSettings
 * @see com.gradle.maven.extension.api.scan.BuildScanCaptureSettings
 */
public interface BuildScanCaptureAdapter {

    void setGoalInputFiles(boolean capture);

    boolean isGoalInputFiles();

    void setBuildLogging(boolean capture);

    boolean isBuildLogging();

    void setTestLogging(boolean capture);

    boolean isTestLogging();

}
