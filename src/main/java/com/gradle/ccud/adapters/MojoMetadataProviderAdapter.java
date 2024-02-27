package com.gradle.ccud.adapters;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;

import java.util.List;
import java.util.function.Consumer;

public interface MojoMetadataProviderAdapter {

    void provideMetadata(Context context);

    interface Context {
        Object getUnderlyingObject();

        MojoExecution getMojoExecution();

        MavenProject getProject();

        MavenSession getSession();

        void withPlugin(String artifactId, Runnable action);

        Context skipIfTrue(String... propertyNames);

        Context skipIfTrue(List<String> propertyNames);

        Context inputs(Consumer<? super Context.Inputs> action);

        Context outputs(Consumer<? super Context.Outputs> action);

        Context localState(Consumer<? super Context.LocalState> action);

        Context nested(String propertyName, Consumer<? super Context> action);

        Context iterate(String propertyName, Consumer<? super Context> action);

        interface LocalState {
            LocalState files(String propertyName);

            LocalState files(String propertyName, Object value);
        }

        interface Outputs {
            Outputs file(String propertyName);

            Outputs file(String propertyName, Object file);

            Outputs directory(String propertyName);

            Outputs directory(String propertyName, Object directory);

            Outputs cacheable(String reason);

            Outputs notCacheableBecause(String reason);

            Outputs storeEnabled(boolean enabled);
        }

        interface FileSet {
            FileSet includesProperty(String includePropertyName);

            FileSet include(List<String> includePatterns);

            FileSet include(String... includePatterns);

            FileSet excludesProperty(String excludePropertyName);

            FileSet exclude(List<String> excludePatterns);

            FileSet exclude(String... excludePatterns);

            FileSet normalizationStrategy(NormalizationStrategy normalizationStrategy);

            FileSet emptyDirectoryHandling(EmptyDirectoryHandling emptyDirectoryHandling);

            FileSet lineEndingHandling(LineEndingHandling lineEndingHandling);

            enum LineEndingHandling {
                DEFAULT,
                NORMALIZE
            }

            enum EmptyDirectoryHandling {
                DEFAULT,
                IGNORE
            }

            enum NormalizationStrategy {
                ABSOLUTE_PATH,
                CLASSPATH,
                COMPILE_CLASSPATH,
                IGNORED_PATH,
                NAME_ONLY,
                RELATIVE_PATH
            }
        }

        interface Inputs {
            Inputs properties(String... propertyNames);

            Inputs property(String propertyName, Object value);

            Inputs fileSet(String propertyName, Consumer<FileSet> action);

            Inputs fileSet(String propertyName, Object files, Consumer<FileSet> action);

            Inputs ignore(String... propertyNames);
        }
    }

}
