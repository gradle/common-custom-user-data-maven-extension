package com.gradle.ccud.adapters;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

import java.util.List;
import java.util.function.Consumer;

public interface NormalizationProviderAdapter {

    void configureNormalization(Context context);

    interface SystemPropertiesNormalization {
        SystemPropertiesNormalization setIgnoredKeys(String... systemPropertyNames);

        SystemPropertiesNormalization setIgnoredKeys(List<String> systemPropertyNames);

        SystemPropertiesNormalization addIgnoredKeys(String... systemPropertyNames);

        SystemPropertiesNormalization addIgnoredKeys(List<String> systemPropertyNames);
    }

    interface RuntimeClasspathNormalization {
        RuntimeClasspathNormalization setIgnoredFiles(List<String> ignoredFiles);

        RuntimeClasspathNormalization setIgnoredFiles(String... ignoredFiles);

        RuntimeClasspathNormalization addIgnoredFiles(List<String> ignoredFiles);

        RuntimeClasspathNormalization addIgnoredFiles(String... ignoredFiles);

        RuntimeClasspathNormalization addPropertiesNormalization(String path, List<String> ignoredProperties);

        RuntimeClasspathNormalization addPropertiesNormalization(String path, String... ignoredProperties);

        RuntimeClasspathNormalization configureMetaInf(Consumer<RuntimeClasspathNormalization.MetaInf> action);

        interface MetaInf {
            RuntimeClasspathNormalization.MetaInf setIgnoredAttributes(List<String> ignoredAttributes);

            RuntimeClasspathNormalization.MetaInf setIgnoredAttributes(String... ignoredAttributes);

            RuntimeClasspathNormalization.MetaInf addIgnoredAttributes(List<String> ignoredAttributes);

            RuntimeClasspathNormalization.MetaInf addIgnoredAttributes(String... ignoredAttributes);

            RuntimeClasspathNormalization.MetaInf setIgnoredProperties(List<String> ignoredProperties);

            RuntimeClasspathNormalization.MetaInf setIgnoredProperties(String... ignoredProperties);

            RuntimeClasspathNormalization.MetaInf addIgnoredProperties(List<String> ignoredProperties);

            RuntimeClasspathNormalization.MetaInf addIgnoredProperties(String... ignoredProperties);

            RuntimeClasspathNormalization.MetaInf setIgnoreManifest(boolean ignoreManifest);

            RuntimeClasspathNormalization.MetaInf setIgnoreCompletely(boolean ignoreCompletely);
        }
    }

    interface Context {
        MavenProject getProject();

        MavenSession getSession();

        Context configureRuntimeClasspathNormalization(Consumer<RuntimeClasspathNormalization> action);

        Context configureSystemPropertiesNormalization(Consumer<SystemPropertiesNormalization> action);
    }
}
