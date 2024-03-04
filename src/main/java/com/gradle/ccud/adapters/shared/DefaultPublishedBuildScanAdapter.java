package com.gradle.ccud.adapters.shared;

import com.gradle.ccud.adapters.PublishedBuildScanAdapter;

import java.net.URI;

public class DefaultPublishedBuildScanAdapter implements PublishedBuildScanAdapter {

    private final String buildScanId;
    private final URI buildScanUri;

    public DefaultPublishedBuildScanAdapter(String buildScanId, URI buildScanUri) {
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