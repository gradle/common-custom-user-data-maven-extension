package com.gradle.ccud.adapters;

import java.net.URI;

/**
 * @see com.gradle.develocity.agent.maven.api.scan.PublishedBuildScan
 * @see com.gradle.maven.extension.api.scan.PublishedBuildScan
 */
public interface PublishedBuildScanAdapter {

    String getBuildScanId();

    URI getBuildScanUri();
}
