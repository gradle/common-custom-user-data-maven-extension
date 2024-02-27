package com.gradle.ccud.adapters;

import java.util.List;

public interface BuildResultAdapter {

    static BuildResultAdapter from(com.gradle.maven.extension.api.scan.BuildResult result) {
        return new DefaultBuildResultAdapter(result.getFailures());
    }

    static BuildResultAdapter from(com.gradle.develocity.agent.maven.api.scan.BuildResult result) {
        return new DefaultBuildResultAdapter(result.getFailures());
    }

    List<Throwable> getFailures();

    class DefaultBuildResultAdapter implements BuildResultAdapter {

        private final List<Throwable> failures;

        private DefaultBuildResultAdapter(List<Throwable> failures) {
            this.failures = failures;
        }

        @Override
        public List<Throwable> getFailures() {
            return failures;
        }
    }

}
