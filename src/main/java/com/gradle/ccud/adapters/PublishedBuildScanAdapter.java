package com.gradle.ccud.adapters;

import java.net.URI;

public interface PublishedBuildScanAdapter {

    static PublishedBuildScanAdapter from(com.gradle.maven.extension.api.scan.PublishedBuildScan scan) {
        return new DefaultPublishedBuildScanAdapter(scan.getBuildScanId(), scan.getBuildScanUri());
    }

    static PublishedBuildScanAdapter from(com.gradle.develocity.agent.maven.api.scan.PublishedBuildScan scan) {
        return new DefaultPublishedBuildScanAdapter(scan.getBuildScanId(), scan.getBuildScanUri());
    }

    String getBuildScanId();

    URI getBuildScanUri();

    class DefaultPublishedBuildScanAdapter implements PublishedBuildScanAdapter {

        private final String buildScanId;
        private final URI buildScanUri;

        private DefaultPublishedBuildScanAdapter(String buildScanId, URI buildScanUri) {
            this.buildScanId = buildScanId;
            this.buildScanUri = buildScanUri;
        }

        @Override
        public String getBuildScanId() {
            return buildScanId;
        }

        @Override
        public URI getBuildScanUri() {
            return buildScanUri;
        }
    }
}
