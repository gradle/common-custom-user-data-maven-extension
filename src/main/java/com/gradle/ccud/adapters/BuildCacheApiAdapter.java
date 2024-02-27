package com.gradle.ccud.adapters;

public interface BuildCacheApiAdapter {

    LocalBuildCacheAdapter getLocal();

    RemoteBuildCacheAdapter getRemote();

    boolean isRequireClean();

    void setRequireClean(boolean requireClean);

    void registerMojoMetadataProvider(MojoMetadataProviderAdapter metadataProvider);

    void registerNormalizationProvider(NormalizationProviderAdapter normalizationProvider);

}
