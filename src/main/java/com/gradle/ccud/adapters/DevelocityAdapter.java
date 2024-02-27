package com.gradle.ccud.adapters;

import java.net.URI;
import java.nio.file.Path;

public interface DevelocityAdapter {

    boolean isEnabled();

    void setEnabled(boolean enabled);

    void setProjectId(String projectId);

    String getProjectId();

    Path getStorageDirectory();

    void setStorageDirectory(Path path);

    default void setServer(String url) {
        setServer(url == null ? null : URI.create(url));
    }

    void setServer(URI url);

    String getServer();

    void setAllowUntrustedServer(boolean allow);

    boolean getAllowUntrustedServer();

    void setAccessKey(String accessKey);

    String getAccessKey();

    BuildScanApiAdapter getBuildScan();

    BuildCacheApiAdapter getBuildCache();

}
