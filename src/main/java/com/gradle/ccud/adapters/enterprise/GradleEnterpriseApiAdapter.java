package com.gradle.ccud.adapters.enterprise;

import com.gradle.ccud.adapters.BuildCacheApiAdapter;
import com.gradle.ccud.adapters.BuildScanApiAdapter;
import com.gradle.ccud.adapters.CoreApiAdapter;
import com.gradle.ccud.adapters.Property;
import com.gradle.maven.extension.api.GradleEnterpriseApi;

import java.net.URI;
import java.nio.file.Path;

public class GradleEnterpriseApiAdapter implements CoreApiAdapter {

    private final GradleEnterpriseApi api;
    private final BuildScanApiAdapter buildScan;
    private final BuildCacheApiAdapter buildCache;
    private final Property<String> projectId;

    public GradleEnterpriseApiAdapter(GradleEnterpriseApi api) {
        this.api = api;
        this.buildScan = new GradleEnterpriseBuildScanApiAdapter(api.getBuildScan());
        this.buildCache = new GradleEnterpriseBuildCacheApiAdapter(api.getBuildCache());
        this.projectId = Property.optional(api, "setProjectId", "getProjectId");
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
        this.projectId.set(projectId);
    }

    @Override
    public String getProjectId() {
        return projectId.get();
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
        return buildCache;
    }

    @Override
    public boolean isDevelocityApi() {
        return false;
    }
}
