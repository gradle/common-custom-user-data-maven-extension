package com.gradle.ccud.adapters.enterprise;

import com.gradle.ccud.adapters.BuildCacheApiAdapter;
import com.gradle.ccud.adapters.LocalBuildCacheAdapter;
import com.gradle.ccud.adapters.MojoMetadataProviderAdapter;
import com.gradle.ccud.adapters.NormalizationProviderAdapter;
import com.gradle.ccud.adapters.Property;
import com.gradle.ccud.adapters.RemoteBuildCacheAdapter;
import com.gradle.ccud.adapters.shared.DefaultCleanupPolicyAdapter;
import com.gradle.ccud.adapters.shared.DefaultCredentialsAdapter;
import com.gradle.ccud.adapters.shared.DefaultLocalBuildCacheAdapter;
import com.gradle.ccud.adapters.shared.DefaultRemoteBuildCacheAdapter;
import com.gradle.ccud.adapters.shared.DefaultServerAdapter;
import com.gradle.maven.extension.api.cache.BuildCacheApi;
import com.gradle.maven.extension.api.cache.CleanupPolicy;
import com.gradle.maven.extension.api.cache.Credentials;
import com.gradle.maven.extension.api.cache.LocalBuildCache;
import com.gradle.maven.extension.api.cache.RemoteBuildCache;
import com.gradle.maven.extension.api.cache.Server;

class GradleEnterpriseBuildCacheApiAdapter implements BuildCacheApiAdapter {

    private final BuildCacheApi buildCache;
    private final LocalBuildCacheAdapter localCache;
    private final RemoteBuildCacheAdapter remoteCache;

    GradleEnterpriseBuildCacheApiAdapter(BuildCacheApi buildCache) {
        this.buildCache = buildCache;
        this.localCache = createLocalCacheAdapter(buildCache);
        this.remoteCache = createRemoteCacheAdapter(buildCache);
    }

    private static DefaultLocalBuildCacheAdapter createLocalCacheAdapter(BuildCacheApi buildCache) {
        LocalBuildCache local = buildCache.getLocal();
        CleanupPolicy cleanupPolicy = local.getCleanupPolicy();
        return new DefaultLocalBuildCacheAdapter(
            new Property<>(local::setEnabled, local::isEnabled),
            new Property<>(local::setStoreEnabled, local::isStoreEnabled),
            new Property<>(local::setDirectory, local::getDirectory),
            new DefaultCleanupPolicyAdapter(
                new Property<>(cleanupPolicy::setEnabled, cleanupPolicy::isEnabled),
                new Property<>(cleanupPolicy::setRetentionPeriod, cleanupPolicy::getRetentionPeriod),
                new Property<>(cleanupPolicy::setCleanupInterval, cleanupPolicy::getCleanupInterval)
            )
        );
    }

    private static DefaultRemoteBuildCacheAdapter createRemoteCacheAdapter(BuildCacheApi buildCache) {
        RemoteBuildCache remote = buildCache.getRemote();
        Server server = remote.getServer();
        return new DefaultRemoteBuildCacheAdapter(
            new Property<>(remote::setEnabled, remote::isEnabled),
            new Property<>(remote::setStoreEnabled, remote::isStoreEnabled),
            createServerAdapter(server)
        );
    }

    private static DefaultServerAdapter createServerAdapter(Server server) {
        Credentials credentials = server.getCredentials();
        return new DefaultServerAdapter(
            new Property<>(server::setServerId, server::getServerId),
            new Property<>(server::setUrl, server::getUrl),
            new Property<>(server::setAllowUntrusted, server::isAllowUntrusted),
            new Property<>(server::setAllowInsecureProtocol, server::isAllowInsecureProtocol),
            new Property<>(server::setUseExpectContinue, server::isUseExpectContinue),
            new DefaultCredentialsAdapter(
                new Property<>(credentials::setUsername, credentials::getUsername),
                new Property<>(credentials::setPassword, credentials::getPassword)
            )
        );
    }

    @Override
    public LocalBuildCacheAdapter getLocal() {
        return localCache;
    }

    @Override
    public RemoteBuildCacheAdapter getRemote() {
        return remoteCache;
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
        buildCache.registerMojoMetadataProvider(ctx -> metadataProvider.provideMetadata(new GradleEnterpriseMojoMetadataContext(ctx)));
    }

    @Override
    public void registerNormalizationProvider(NormalizationProviderAdapter normalizationProvider) {
        buildCache.registerNormalizationProvider(ctx -> normalizationProvider.configureNormalization(new GradleEnterpriseNormalizationContext(ctx)));
    }
}
