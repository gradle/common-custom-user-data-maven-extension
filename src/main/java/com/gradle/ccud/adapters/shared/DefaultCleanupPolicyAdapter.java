package com.gradle.ccud.adapters.shared;

import com.gradle.ccud.adapters.CleanupPolicyAdapter;
import com.gradle.ccud.adapters.Property;

import java.time.Duration;

public class DefaultCleanupPolicyAdapter implements CleanupPolicyAdapter {

    private final Property<Boolean> enabled;
    private final Property<Duration> retentionPeriod;
    private final Property<Duration> cleanupInterval;

    public DefaultCleanupPolicyAdapter(
        Property<Boolean> enabled,
        Property<Duration> retentionPeriod,
        Property<Duration> cleanupInterval
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
