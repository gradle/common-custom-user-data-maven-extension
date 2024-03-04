package com.gradle.ccud.adapters;

import java.util.List;

/**
 * @see com.gradle.develocity.agent.maven.api.scan.BuildResult
 * @see com.gradle.maven.extension.api.scan.BuildResult
 */
public interface BuildResultAdapter {

    List<Throwable> getFailures();

}
