package com.gradle.ccud.adapters;

import java.time.Duration;

/**
 * @see com.gradle.develocity.agent.maven.api.cache.CleanupPolicy
 * @see com.gradle.maven.extension.api.cache.CleanupPolicy
 */
public interface CleanupPolicyAdapter {

    boolean isEnabled();

    void setEnabled(boolean enabled);

    Duration getRetentionPeriod();

    void setRetentionPeriod(Duration retentionPeriod);

    Duration getCleanupInterval();

    void setCleanupInterval(Duration cleanupInterval);

}
