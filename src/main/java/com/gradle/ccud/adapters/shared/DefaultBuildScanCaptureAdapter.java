package com.gradle.ccud.adapters.shared;

import com.gradle.ccud.adapters.BuildScanCaptureAdapter;
import com.gradle.ccud.adapters.Property;

public class DefaultBuildScanCaptureAdapter implements BuildScanCaptureAdapter {

    private final Property<Boolean> goalInputFiles;
    private final Property<Boolean> buildLogging;
    private final Property<Boolean> testLogging;

    public DefaultBuildScanCaptureAdapter(Property<Boolean> goalInputFiles, Property<Boolean> buildLogging, Property<Boolean> testLogging) {
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
