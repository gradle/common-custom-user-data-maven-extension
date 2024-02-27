package com.gradle.ccud.adapters.enterprise;

import com.gradle.ccud.adapters.BuildCacheApiAdapter;
import com.gradle.ccud.adapters.LocalBuildCacheAdapter;
import com.gradle.ccud.adapters.MojoMetadataProviderAdapter;
import com.gradle.ccud.adapters.NormalizationProviderAdapter;
import com.gradle.ccud.adapters.PropertyConfigurator;
import com.gradle.ccud.adapters.RemoteBuildCacheAdapter;
import com.gradle.ccud.adapters.shared.DefaultCleanupPolicyAdapter;
import com.gradle.ccud.adapters.shared.DefaultLocalBuildCacheAdapter;
import com.gradle.maven.extension.api.cache.BuildCacheApi;
import com.gradle.maven.extension.api.cache.CleanupPolicy;
import com.gradle.maven.extension.api.cache.LocalBuildCache;

class GradleEnterpriseBuildCacheApiAdapter implements BuildCacheApiAdapter {

    private final BuildCacheApi buildCache;
    private final LocalBuildCacheAdapter localCache;

    GradleEnterpriseBuildCacheApiAdapter(BuildCacheApi buildCache) {
        this.buildCache = buildCache;

        LocalBuildCache local = buildCache.getLocal();
        CleanupPolicy cleanupPolicy = local.getCleanupPolicy();
        this.localCache = new DefaultLocalBuildCacheAdapter(
            new PropertyConfigurator<>(local::setEnabled, local::isEnabled),
            new PropertyConfigurator<>(local::setStoreEnabled, local::isStoreEnabled),
            new PropertyConfigurator<>(local::setDirectory, local::getDirectory),
            new DefaultCleanupPolicyAdapter(
                new PropertyConfigurator<>(cleanupPolicy::setEnabled, cleanupPolicy::isEnabled),
                new PropertyConfigurator<>(cleanupPolicy::setRetentionPeriod, cleanupPolicy::getRetentionPeriod),
                new PropertyConfigurator<>(cleanupPolicy::setCleanupInterval, cleanupPolicy::getCleanupInterval)
            )
        );
    }

    @Override
    public LocalBuildCacheAdapter getLocal() {
        return localCache;
    }

    @Override
    public RemoteBuildCacheAdapter getRemote() {
        // TODO pshevche
        return null;
    }

    @Override
    public boolean isRequireClean() {
        return buildCache.isRequireClean();
    }

    @Override
    public void setRequireClean(boolean requireClean) {
        buildCache.setRequireClean(requireClean);
    }

    @Override
    public void registerMojoMetadataProvider(MojoMetadataProviderAdapter metadataProvider) {
        // TODO pshevche
    }

    @Override
    public void registerNormalizationProvider(NormalizationProviderAdapter normalizationProvider) {
        // TODO pshevche
    }
}
