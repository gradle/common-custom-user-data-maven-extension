package com.gradle.ccud.adapters;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @see com.gradle.develocity.agent.maven.api.cache.NormalizationProvider
 * @see com.gradle.maven.extension.api.cache.NormalizationProvider
 */
public interface NormalizationProviderAdapter {

    void configureNormalization(Context context);

    interface SystemPropertiesNormalization {
        default SystemPropertiesNormalization setIgnoredKeys(String... systemPropertyNames) {
            return setIgnoredKeys(Arrays.asList(systemPropertyNames));
        }

        SystemPropertiesNormalization setIgnoredKeys(List<String> systemPropertyNames);

        default SystemPropertiesNormalization addIgnoredKeys(String... systemPropertyNames) {
            return addIgnoredKeys(Arrays.asList(systemPropertyNames));
        }

        SystemPropertiesNormalization addIgnoredKeys(List<String> systemPropertyNames);
    }

    interface RuntimeClasspathNormalization {
        RuntimeClasspathNormalization setIgnoredFiles(List<String> ignoredFiles);

        default RuntimeClasspathNormalization setIgnoredFiles(String... ignoredFiles) {
            return setIgnoredFiles(Arrays.asList(ignoredFiles));
        }

        RuntimeClasspathNormalization addIgnoredFiles(List<String> ignoredFiles);

        default RuntimeClasspathNormalization addIgnoredFiles(String... ignoredFiles) {
            return addIgnoredFiles(Arrays.asList(ignoredFiles));
        }

        RuntimeClasspathNormalization addPropertiesNormalization(String path, List<String> ignoredProperties);

        default RuntimeClasspathNormalization addPropertiesNormalization(String path, String... ignoredProperties) {
            return addPropertiesNormalization(path, Arrays.asList(ignoredProperties));
        }

        RuntimeClasspathNormalization configureMetaInf(Consumer<RuntimeClasspathNormalization.MetaInf> action);

        interface MetaInf {
            RuntimeClasspathNormalization.MetaInf setIgnoredAttributes(List<String> ignoredAttributes);

            default RuntimeClasspathNormalization.MetaInf setIgnoredAttributes(String... ignoredAttributes) {
                return setIgnoredAttributes(Arrays.asList(ignoredAttributes));
            }

            RuntimeClasspathNormalization.MetaInf addIgnoredAttributes(List<String> ignoredAttributes);

            default RuntimeClasspathNormalization.MetaInf addIgnoredAttributes(String... ignoredAttributes) {
                return addIgnoredAttributes(Arrays.asList(ignoredAttributes));
            }

            RuntimeClasspathNormalization.MetaInf setIgnoredProperties(List<String> ignoredProperties);

            default RuntimeClasspathNormalization.MetaInf setIgnoredProperties(String... ignoredProperties) {
                return setIgnoredProperties(Arrays.asList(ignoredProperties));
            }

            RuntimeClasspathNormalization.MetaInf addIgnoredProperties(List<String> ignoredProperties);

            default RuntimeClasspathNormalization.MetaInf addIgnoredProperties(String... ignoredProperties) {
                return addIgnoredProperties(Arrays.asList(ignoredProperties));
            }

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
