package com.gradle.ccud.adapters;

/**
 * @see com.gradle.develocity.agent.maven.api.cache.RemoteBuildCache
 * @see com.gradle.maven.extension.api.cache.RemoteBuildCache
 */
public interface RemoteBuildCacheAdapter {

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isStoreEnabled();

    void setStoreEnabled(boolean storeEnabled);

    ServerAdapter getServer();

}
