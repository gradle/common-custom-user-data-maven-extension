package com.gradle;

import com.gradle.ccud.adapters.BuildCacheApiAdapter;
import com.gradle.ccud.adapters.BuildScanApiAdapter;
import com.gradle.ccud.adapters.DevelocityAdapter;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

abstract class CommonCustomUserDataListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonCustomUserDataListener.class);
    private static final AtomicBoolean SINGLE_APPLICATION_LOCK = new AtomicBoolean(false);

    protected void configure(DevelocityAdapter api, MavenSession session, CustomConfigurationSpec customConfigurationSpec) throws MavenExecutionException {
        if (SINGLE_APPLICATION_LOCK.compareAndSet(false, true)) {
            LOGGER.debug("Executing extension: " + getClass().getSimpleName());
            CustomDevelocityConfig customDevelocityConfig = new CustomDevelocityConfig();

            LOGGER.debug("Configuring {}", customConfigurationSpec.displayName);
            customDevelocityConfig.configureDevelocity(api);
            LOGGER.debug("Finished configuring {}", customConfigurationSpec.displayName);

            LOGGER.debug("Configuring build scan publishing and applying build scan enhancements");
            BuildScanApiAdapter buildScan = api.getBuildScan();
            customDevelocityConfig.configureBuildScanPublishing(buildScan);
            new CustomBuildScanEnhancements(buildScan, session).apply();
            LOGGER.debug("Finished configuring build scan publishing and applying build scan enhancements");

            LOGGER.debug("Configuring build cache");
            BuildCacheApiAdapter buildCache = api.getBuildCache();
            customDevelocityConfig.configureBuildCache(buildCache);
            LOGGER.debug("Finished configuring build cache");

            GroovyScriptUserData.evaluate(session, api, LOGGER, customConfigurationSpec);
        }
    }

}
