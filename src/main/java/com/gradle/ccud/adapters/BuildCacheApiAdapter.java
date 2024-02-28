package com.gradle.ccud.adapters;

/**
 * @see com.gradle.develocity.agent.maven.api.cache.BuildCacheApi
 * @see com.gradle.maven.extension.api.cache.BuildCacheApi
 */
public interface BuildCacheApiAdapter {

    LocalBuildCacheAdapter getLocal();

    RemoteBuildCacheAdapter getRemote();

    boolean isRequireClean();

    void setRequireClean(boolean requireClean);

    void registerMojoMetadataProvider(MojoMetadataProviderAdapter metadataProvider);

    void registerNormalizationProvider(NormalizationProviderAdapter normalizationProvider);

}
