package com.gradle.ccud.adapters;

import java.io.File;

/**
 * @see com.gradle.develocity.agent.maven.api.cache.LocalBuildCache
 * @see com.gradle.maven.extension.api.cache.LocalBuildCache
 */
public interface LocalBuildCacheAdapter {

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isStoreEnabled();

    void setStoreEnabled(boolean storeEnabled);

    File getDirectory();

    void setDirectory(File directory);

    CleanupPolicyAdapter getCleanupPolicy();

}
