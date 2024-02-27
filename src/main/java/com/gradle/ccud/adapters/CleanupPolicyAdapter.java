package com.gradle.ccud.adapters;

import java.time.Duration;

public interface CleanupPolicyAdapter {

    boolean isEnabled();

    void setEnabled(boolean enabled);

    Duration getRetentionPeriod();

    void setRetentionPeriod(Duration retentionPeriod);

    Duration getCleanupInterval();

    void setCleanupInterval(Duration cleanupInterval);

}
