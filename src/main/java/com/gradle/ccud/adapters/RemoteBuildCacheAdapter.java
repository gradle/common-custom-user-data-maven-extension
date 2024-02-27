package com.gradle.ccud.adapters;

public interface RemoteBuildCacheAdapter {

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isStoreEnabled();

    void setStoreEnabled(boolean storeEnabled);

    ServerAdapter getServer();

}
