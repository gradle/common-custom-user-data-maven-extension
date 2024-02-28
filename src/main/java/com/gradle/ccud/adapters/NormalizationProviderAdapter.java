package com.gradle.ccud.adapters;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.project.MavenProject;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public interface NormalizationProviderAdapter {

    void configureNormalization(Context context);

    interface SystemPropertiesNormalization {
        default SystemPropertiesNormalization setIgnoredKeys(String... systemPropertyNames) {
            setIgnoredKeys(Arrays.asList(systemPropertyNames));
            return this;
        }

        SystemPropertiesNormalization setIgnoredKeys(List<String> systemPropertyNames);

        default SystemPropertiesNormalization addIgnoredKeys(String... systemPropertyNames) {
            addIgnoredKeys(Arrays.asList(systemPropertyNames));
            return this;
        }

        SystemPropertiesNormalization addIgnoredKeys(List<String> systemPropertyNames);
    }

    interface RuntimeClasspathNormalization {
        RuntimeClasspathNormalization setIgnoredFiles(List<String> ignoredFiles);

        default RuntimeClasspathNormalization setIgnoredFiles(String... ignoredFiles) {
            setIgnoredFiles(Arrays.asList(ignoredFiles));
            return this;
        }

        RuntimeClasspathNormalization addIgnoredFiles(List<String> ignoredFiles);

        default RuntimeClasspathNormalization addIgnoredFiles(String... ignoredFiles) {
            addIgnoredFiles(Arrays.asList(ignoredFiles));
            return this;
        }

        RuntimeClasspathNormalization addPropertiesNormalization(String path, List<String> ignoredProperties);

        default RuntimeClasspathNormalization addPropertiesNormalization(String path, String... ignoredProperties) {
            addPropertiesNormalization(path, Arrays.asList(ignoredProperties));
            return this;
        }

        RuntimeClasspathNormalization configureMetaInf(Consumer<RuntimeClasspathNormalization.MetaInf> action);

        interface MetaInf {
            RuntimeClasspathNormalization.MetaInf setIgnoredAttributes(List<String> ignoredAttributes);

            default RuntimeClasspathNormalization.MetaInf setIgnoredAttributes(String... ignoredAttributes) {
                setIgnoredAttributes(Arrays.asList(ignoredAttributes));
                return this;
            }

            RuntimeClasspathNormalization.MetaInf addIgnoredAttributes(List<String> ignoredAttributes);

            default RuntimeClasspathNormalization.MetaInf addIgnoredAttributes(String... ignoredAttributes) {
                addIgnoredAttributes(Arrays.asList(ignoredAttributes));
                return this;
            }

            RuntimeClasspathNormalization.MetaInf setIgnoredProperties(List<String> ignoredProperties);

            default RuntimeClasspathNormalization.MetaInf setIgnoredProperties(String... ignoredProperties) {
                setIgnoredProperties(Arrays.asList(ignoredProperties));
                return this;
            }

            RuntimeClasspathNormalization.MetaInf addIgnoredProperties(List<String> ignoredProperties);

            default RuntimeClasspathNormalization.MetaInf addIgnoredProperties(String... ignoredProperties) {
                addIgnoredProperties(Arrays.asList(ignoredProperties));
                return this;
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
