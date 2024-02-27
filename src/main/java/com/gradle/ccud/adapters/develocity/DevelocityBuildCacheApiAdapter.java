package com.gradle.ccud.adapters.develocity;

import com.gradle.ccud.adapters.BuildCacheApiAdapter;
import com.gradle.ccud.adapters.LocalBuildCacheAdapter;
import com.gradle.ccud.adapters.MojoMetadataProviderAdapter;
import com.gradle.ccud.adapters.NormalizationProviderAdapter;
import com.gradle.ccud.adapters.RemoteBuildCacheAdapter;
import com.gradle.develocity.agent.maven.api.cache.BuildCacheApi;

class DevelocityBuildCacheApiAdapter implements BuildCacheApiAdapter {

    private final BuildCacheApi buildCache;

    DevelocityBuildCacheApiAdapter(BuildCacheApi buildCache) {
        this.buildCache = buildCache;
    }

    @Override
    public LocalBuildCacheAdapter getLocal() {
        // TODO pshevche
        return null;
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
