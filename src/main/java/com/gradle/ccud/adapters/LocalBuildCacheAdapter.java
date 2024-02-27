package com.gradle.ccud.adapters;

import java.io.File;

public interface LocalBuildCacheAdapter {

    boolean isEnabled();

    void setEnabled(boolean enabled);

    boolean isStoreEnabled();

    void setStoreEnabled(boolean storeEnabled);

    File getDirectory();

    void setDirectory(File directory);

    CleanupPolicyAdapter getCleanupPolicy();

}
