package com.gradle.ccud.adapters.shared;

import com.gradle.ccud.adapters.BuildScanCaptureAdapter;
import com.gradle.ccud.adapters.PropertyConfigurator;

public class DefaultBuildScanCaptureAdapter implements BuildScanCaptureAdapter {

    private final PropertyConfigurator<Boolean> goalInputFiles;
    private final PropertyConfigurator<Boolean> buildLogging;
    private final PropertyConfigurator<Boolean> testLogging;

    public DefaultBuildScanCaptureAdapter(PropertyConfigurator<Boolean> goalInputFiles, PropertyConfigurator<Boolean> buildLogging, PropertyConfigurator<Boolean> testLogging) {
        this.goalInputFiles = goalInputFiles;
        this.buildLogging = buildLogging;
        this.testLogging = testLogging;
    }

    @Override
    public void setGoalInputFiles(boolean capture) {
        goalInputFiles.set(capture);
    }

    @Override
    public boolean isGoalInputFiles() {
        return goalInputFiles.get();
    }

    @Override
    public void setBuildLogging(boolean capture) {
        buildLogging.set(capture);
    }

    @Override
    public boolean isBuildLogging() {
        return buildLogging.get();
    }

    @Override
    public void setTestLogging(boolean capture) {
        testLogging.set(capture);
    }

    @Override
    public boolean isTestLogging() {
        return testLogging.get();
    }
}
