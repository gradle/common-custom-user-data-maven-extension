package com.gradle.ccud.adapters.shared;

import com.gradle.ccud.adapters.CleanupPolicyAdapter;
import com.gradle.ccud.adapters.LocalBuildCacheAdapter;
import com.gradle.ccud.adapters.Property;

import java.io.File;

public class DefaultLocalBuildCacheAdapter implements LocalBuildCacheAdapter {

    private final Property<Boolean> enabled;
    private final Property<Boolean> storeEnabled;
    private final Property<File> directory;
    private final CleanupPolicyAdapter cleanupPolicyAdapter;

    public DefaultLocalBuildCacheAdapter(
        Property<Boolean> enabled,
        Property<Boolean> storeEnabled,
        Property<File> directory,
        CleanupPolicyAdapter cleanupPolicyAdapter
    ) {
        this.enabled = enabled;
        this.storeEnabled = storeEnabled;
        this.directory = directory;
        this.cleanupPolicyAdapter = cleanupPolicyAdapter;
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
    public File getDirectory() {
        return directory.get();
    }

    @Override
    public void setDirectory(File directory) {
        this.directory.set(directory);
    }

    @Override
    public CleanupPolicyAdapter getCleanupPolicy() {
        return cleanupPolicyAdapter;
    }
}
