package com.gradle.ccud.adapters.shared;

import com.gradle.ccud.adapters.Property;
import com.gradle.ccud.adapters.RemoteBuildCacheAdapter;
import com.gradle.ccud.adapters.ServerAdapter;

public class DefaultRemoteBuildCacheAdapter implements RemoteBuildCacheAdapter {

    private final Property<Boolean> enabled;
    private final Property<Boolean> storeEnabled;
    private final ServerAdapter serverAdapter;

    public DefaultRemoteBuildCacheAdapter(
        Property<Boolean> enabled,
        Property<Boolean> storeEnabled,
        ServerAdapter serverAdapter
    ) {
        this.enabled = enabled;
        this.storeEnabled = storeEnabled;
        this.serverAdapter = serverAdapter;
    }

    @Override
    public boolean isEnabled() {
        return enabled.get();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled.set(enabled);
    }

    @Override
    public boolean isStoreEnabled() {
        return storeEnabled.get();
    }

    @Override
    public void setStoreEnabled(boolean storeEnabled) {
        this.storeEnabled.set(storeEnabled);
    }

    @Override
    public ServerAdapter getServer() {
        return serverAdapter;
    }
}
