package com.gradle.ccud.adapters.shared;

import com.gradle.ccud.adapters.CleanupPolicyAdapter;
import com.gradle.ccud.adapters.PropertyConfigurator;

import java.time.Duration;

public class DefaultCleanupPolicyAdapter implements CleanupPolicyAdapter {

    private final PropertyConfigurator<Boolean> enabled;
    private final PropertyConfigurator<Duration> retentionPeriod;
    private final PropertyConfigurator<Duration> cleanupInterval;

    public DefaultCleanupPolicyAdapter(
        PropertyConfigurator<Boolean> enabled,
        PropertyConfigurator<Duration> retentionPeriod,
        PropertyConfigurator<Duration> cleanupInterval
    ) {
        this.enabled = enabled;
        this.retentionPeriod = retentionPeriod;
        this.cleanupInterval = cleanupInterval;
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
    public Duration getRetentionPeriod() {
        return retentionPeriod.get();
    }

    @Override
    public void setRetentionPeriod(Duration retentionPeriod) {
        this.retentionPeriod.set(retentionPeriod);
    }

    @Override
    public Duration getCleanupInterval() {
        return cleanupInterval.get();
    }

    @Override
    public void setCleanupInterval(Duration cleanupInterval) {
        this.cleanupInterval.set(cleanupInterval);
    }
}
