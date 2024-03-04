package com.gradle;

import java.util.Optional;

enum CustomConfigurationSpec {
    GRADLE_ENTERPRISE(
        "Gradle Enterprise",
        "gradle-enterprise-custom-user-data",
        "gradleEnterprise",
        Optional.empty()
    ),
    DEVELOCITY(
        "Develocity",
        "develocity-custom-user-data",
        "develocity",
        Optional.of(GRADLE_ENTERPRISE)
    );

    final String displayName;
    final String groovyScriptName;
    final String apiVariableName;
    final Optional<CustomConfigurationSpec> fallbackScript;

    CustomConfigurationSpec(
        String displayName,
        String groovyScriptName,
        String apiVariableName,
        Optional<CustomConfigurationSpec> fallbackScript
    ) {
        this.displayName = displayName;
        this.groovyScriptName = groovyScriptName;
        this.apiVariableName = apiVariableName;
        this.fallbackScript = fallbackScript;
    }
}
