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

import static com.gradle.ccud.adapters.ReflectionUtils.withMethodSupported;

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
            Property.create(local::setEnabled, local::isEnabled),
            Property.optional(local, "setStoreEnabled", "isStoreEnabled"),
            Property.create(local::setDirectory, local::getDirectory),
            new DefaultCleanupPolicyAdapter(
                Property.create(cleanupPolicy::setEnabled, cleanupPolicy::isEnabled),
                Property.create(cleanupPolicy::setRetentionPeriod, cleanupPolicy::getRetentionPeriod),
                Property.create(cleanupPolicy::setCleanupInterval, cleanupPolicy::getCleanupInterval)
            )
        );
    }

    private static DefaultRemoteBuildCacheAdapter createRemoteCacheAdapter(BuildCacheApi buildCache) {
        RemoteBuildCache remote = buildCache.getRemote();
        Server server = remote.getServer();
        return new DefaultRemoteBuildCacheAdapter(
            Property.create(remote::setEnabled, remote::isEnabled),
            Property.create(remote::setStoreEnabled, remote::isStoreEnabled),
            createServerAdapter(server)
        );
    }

    private static DefaultServerAdapter createServerAdapter(Server server) {
        Credentials credentials = server.getCredentials();
        return new DefaultServerAdapter(
            Property.create(server::setServerId, server::getServerId),
            Property.create(server::setUrl, server::getUrl),
            Property.create(server::setAllowUntrusted, server::isAllowUntrusted),
            Property.optional(server, "setAllowInsecureProtocol", "isAllowInsecureProtocol"),
            Property.optional(server, "setUseExpectContinue", "isUseExpectContinue"),
            new DefaultCredentialsAdapter(
                Property.create(credentials::setUsername, credentials::getUsername),
                Property.create(credentials::setPassword, credentials::getPassword)
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
        withMethodSupported(
            buildCache,
            "registerMojoMetadataProvider",
            () -> buildCache.registerMojoMetadataProvider(ctx -> metadataProvider.provideMetadata(new GradleEnterpriseMojoMetadataContext(ctx)))
        );
    }

    @Override
    public void registerNormalizationProvider(NormalizationProviderAdapter normalizationProvider) {
        withMethodSupported(
            buildCache,
            "registerNormalizationProvider",
            () -> buildCache.registerNormalizationProvider(ctx -> normalizationProvider.configureNormalization(new GradleEnterpriseNormalizationContext(ctx)))
        );
    }
}
