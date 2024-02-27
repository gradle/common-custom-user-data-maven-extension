package com.gradle.ccud.adapters.shared;

import com.gradle.ccud.adapters.BuildResultAdapter;

import java.util.List;

public class DefaultBuildResultAdapter implements BuildResultAdapter {

    private final List<Throwable> failures;

    public DefaultBuildResultAdapter(List<Throwable> failures) {
        this.failures = failures;
    }

    @Override
    public List<Throwable> getFailures() {
        return failures;
    }
}