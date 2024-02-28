package com.gradle;

enum CustomConfigurationSpec {
    GRADLE_ENTERPRISE(
        "Gradle Enterprise",
        "gradle-enterprise-custom-user-data",
        "gradleEnterprise"
    ),
    DEVELOCITY(
        "Develocity",
        "develocity-custom-user-data",
        "develocity"
    );

    final String displayName;
    final String groovyScriptName;
    final String apiVariableName;

    CustomConfigurationSpec(String displayName, String groovyScriptName, String apiVariableName) {
        this.displayName = displayName;
        this.groovyScriptName = groovyScriptName;
        this.apiVariableName = apiVariableName;
    }
}
