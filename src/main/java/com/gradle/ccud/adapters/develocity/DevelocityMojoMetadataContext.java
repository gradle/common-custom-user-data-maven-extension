package com.gradle.ccud.adapters.develocity;

import com.gradle.ccud.adapters.MojoMetadataProviderAdapter;
import com.gradle.develocity.agent.maven.api.cache.MojoMetadataProvider;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;

import java.util.List;
import java.util.function.Consumer;

class DevelocityMojoMetadataContext implements MojoMetadataProviderAdapter.Context {
    private final MojoMetadataProvider.Context ctx;

    DevelocityMojoMetadataContext(MojoMetadataProvider.Context ctx) {
        this.ctx = ctx;
    }

    @Override
    public Object getUnderlyingObject() {
        return ctx.getUnderlyingObject();
    }

    @Override
    public MojoExecution getMojoExecution() {
        return ctx.getMojoExecution();
    }

    @Override
    public MavenProject getProject() {
        return ctx.getProject();
    }

    @Override
    public MavenSession getSession() {
        return ctx.getSession();
    }

    @Override
    public void withPlugin(String artifactId, Runnable action) {
        ctx.withPlugin(artifactId, action);
    }

    @Override
    public MojoMetadataProviderAdapter.Context skipIfTrue(List<String> propertyNames) {
        ctx.skipIfTrue(propertyNames);
        return this;
    }

    @Override
    public MojoMetadataProviderAdapter.Context inputs(Consumer<? super MojoMetadataProviderAdapter.Context.Inputs> action) {
        ctx.inputs(inputs -> action.accept(new Inputs(inputs)));
        return this;
    }

    @Override
    public MojoMetadataProviderAdapter.Context outputs(Consumer<? super MojoMetadataProviderAdapter.Context.Outputs> action) {
        ctx.outputs(outputs -> action.accept(new Outputs(outputs)));
        return this;
    }

    @Override
    public MojoMetadataProviderAdapter.Context localState(Consumer<? super MojoMetadataProviderAdapter.Context.LocalState> action) {
        ctx.localState(state -> action.accept(new LocalState(state)));
        return this;
    }

    @Override
    public MojoMetadataProviderAdapter.Context nested(String propertyName, Consumer<? super MojoMetadataProviderAdapter.Context> action) {
        ctx.nested(propertyName, ctx -> action.accept(new DevelocityMojoMetadataContext(ctx)));
        return this;
    }

    @Override
    public MojoMetadataProviderAdapter.Context iterate(String propertyName, Consumer<? super MojoMetadataProviderAdapter.Context> action) {
        ctx.iterate(propertyName, ctx -> action.accept(new DevelocityMojoMetadataContext(ctx)));
        return this;
    }

    private static class Inputs implements MojoMetadataProviderAdapter.Context.Inputs {

        private final MojoMetadataProvider.Context.Inputs inputs;

        private Inputs(MojoMetadataProvider.Context.Inputs inputs) {
            this.inputs = inputs;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.Inputs properties(String... propertyNames) {
            inputs.properties(propertyNames);
            return this;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.Inputs property(String propertyName, Object value) {
            inputs.property(propertyName, value);
            return this;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.Inputs fileSet(String propertyName, Consumer<MojoMetadataProviderAdapter.Context.FileSet> action) {
            inputs.fileSet(propertyName, fileSet -> action.accept(new FileSet(fileSet)));
            return this;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.Inputs fileSet(String propertyName, Object files, Consumer<MojoMetadataProviderAdapter.Context.FileSet> action) {
            inputs.fileSet(propertyName, files, fileSet -> action.accept(new FileSet(fileSet)));
            return this;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.Inputs ignore(String... propertyNames) {
            inputs.ignore(propertyNames);
            return this;
        }
    }

    private static class Outputs implements MojoMetadataProviderAdapter.Context.Outputs {

        private final MojoMetadataProvider.Context.Outputs outputs;

        private Outputs(MojoMetadataProvider.Context.Outputs outputs) {
            this.outputs = outputs;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.Outputs file(String propertyName) {
            outputs.file(propertyName);
            return this;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.Outputs file(String propertyName, Object file) {
            outputs.file(propertyName, file);
            return this;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.Outputs directory(String propertyName) {
            outputs.directory(propertyName);
            return this;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.Outputs directory(String propertyName, Object directory) {
            outputs.directory(propertyName, directory);
            return this;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.Outputs cacheable(String reason) {
            outputs.cacheable(reason);
            return this;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.Outputs notCacheableBecause(String reason) {
            outputs.notCacheableBecause(reason);
            return this;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.Outputs storeEnabled(boolean enabled) {
            outputs.storeEnabled(enabled);
            return this;
        }
    }

    private static class LocalState implements MojoMetadataProviderAdapter.Context.LocalState {

        private final MojoMetadataProvider.Context.LocalState state;

        private LocalState(MojoMetadataProvider.Context.LocalState state) {
            this.state = state;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.LocalState files(String propertyName) {
            state.files(propertyName);
            return this;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.LocalState files(String propertyName, Object value) {
            state.files(propertyName, value);
            return this;
        }
    }

    private static class FileSet implements MojoMetadataProviderAdapter.Context.FileSet {

        private final MojoMetadataProvider.Context.FileSet fileSet;

        private FileSet(MojoMetadataProvider.Context.FileSet fileSet) {
            this.fileSet = fileSet;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.FileSet includesProperty(String includePropertyName) {
            fileSet.includesProperty(includePropertyName);
            return this;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.FileSet include(List<String> includePatterns) {
            fileSet.include(includePatterns);
            return this;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.FileSet excludesProperty(String excludePropertyName) {
            fileSet.excludesProperty(excludePropertyName);
            return this;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.FileSet exclude(List<String> excludePatterns) {
            fileSet.exclude(excludePatterns);
            return this;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.FileSet normalizationStrategy(NormalizationStrategy normalizationStrategy) {
            fileSet.normalizationStrategy(MojoMetadataProvider.Context.FileSet.NormalizationStrategy.valueOf(normalizationStrategy.name()));
            return this;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.FileSet emptyDirectoryHandling(EmptyDirectoryHandling emptyDirectoryHandling) {
            fileSet.emptyDirectoryHandling(MojoMetadataProvider.Context.FileSet.EmptyDirectoryHandling.valueOf(emptyDirectoryHandling.name()));
            return this;
        }

        @Override
        public MojoMetadataProviderAdapter.Context.FileSet lineEndingHandling(LineEndingHandling lineEndingHandling) {
            fileSet.lineEndingHandling(MojoMetadataProvider.Context.FileSet.LineEndingHandling.valueOf(lineEndingHandling.name()));
            return this;
        }
    }
}
