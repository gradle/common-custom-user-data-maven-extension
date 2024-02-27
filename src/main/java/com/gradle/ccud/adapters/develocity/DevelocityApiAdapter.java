package com.gradle.ccud.adapters.develocity;

import com.gradle.ccud.adapters.BuildCacheApiAdapter;
import com.gradle.ccud.adapters.BuildScanApiAdapter;
import com.gradle.ccud.adapters.DevelocityAdapter;
import com.gradle.develocity.agent.maven.api.DevelocityApi;

import java.net.URI;
import java.nio.file.Path;

public class DevelocityApiAdapter implements DevelocityAdapter {

    private final DevelocityApi api;
    private final BuildScanApiAdapter buildScan;

    public DevelocityApiAdapter(DevelocityApi api) {
        this.api = api;
        this.buildScan = new DevelocityBuildScanApiAdapter(api.getBuildScan());
    }

    @Override
    public boolean isEnabled() {
        return api.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        api.setEnabled(enabled);
    }

    @Override
    public void setProjectId(String projectId) {
        api.setProjectId(projectId);
    }

    @Override
    public String getProjectId() {
        return api.getProjectId();
    }

    @Override
    public Path getStorageDirectory() {
        return api.getStorageDirectory();
    }

    @Override
    public void setStorageDirectory(Path path) {
        api.setStorageDirectory(path);
    }

    @Override
    public void setServer(URI url) {
        api.setServer(url);
    }

    @Override
    public String getServer() {
        return api.getServer();
    }

    @Override
    public void setAllowUntrustedServer(boolean allow) {
        api.setAllowUntrustedServer(allow);
    }

    @Override
    public boolean getAllowUntrustedServer() {
        return api.getAllowUntrustedServer();
    }

    @Override
    public void setAccessKey(String accessKey) {
        api.setAccessKey(accessKey);
    }

    @Override
    public String getAccessKey() {
        return api.getAccessKey();
    }

    @Override
    public BuildScanApiAdapter getBuildScan() {
        return buildScan;
    }

    @Override
    public BuildCacheApiAdapter getBuildCache() {
        // TODO pshevche
        return null;
    }
}
