package com.gradle;

import com.gradle.develocity.agent.maven.adapters.BuildCacheApiAdapter;
import com.gradle.develocity.agent.maven.adapters.BuildScanApiAdapter;
import com.gradle.develocity.agent.maven.adapters.DevelocityAdapter;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;

import static com.gradle.CommonCustomUserDataDevelocityLogger.LOGGER;

abstract class CommonCustomUserDataListener {

    protected void configure(DevelocityAdapter api, MavenSession session, CustomConfigurationSpec customConfigurationSpec) throws MavenExecutionException {
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
