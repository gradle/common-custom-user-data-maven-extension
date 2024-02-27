package com.gradle.ccud.adapters.enterprise;

import com.gradle.ccud.adapters.BuildResultAdapter;
import com.gradle.ccud.adapters.BuildScanApiAdapter;
import com.gradle.ccud.adapters.BuildScanCaptureAdapter;
import com.gradle.ccud.adapters.BuildScanDataObfuscationAdapter;
import com.gradle.ccud.adapters.Property;
import com.gradle.ccud.adapters.PublishedBuildScanAdapter;
import com.gradle.ccud.adapters.shared.DefaultBuildResultAdapter;
import com.gradle.ccud.adapters.shared.DefaultBuildScanCaptureAdapter;
import com.gradle.ccud.adapters.shared.DefaultBuildScanDataObfuscationAdapter;
import com.gradle.ccud.adapters.shared.DefaultPublishedBuildScanAdapter;
import com.gradle.maven.extension.api.scan.BuildScanApi;

import java.net.URI;
import java.util.function.Consumer;

class GradleEnterpriseBuildScanApiAdapter implements BuildScanApiAdapter {

    private final BuildScanApi buildScan;
    private final BuildScanDataObfuscationAdapter obfuscation;
    private final BuildScanCaptureAdapter capture;

    GradleEnterpriseBuildScanApiAdapter(BuildScanApi buildScan) {
        this.buildScan = buildScan;
        this.obfuscation = new DefaultBuildScanDataObfuscationAdapter(
            buildScan.getObfuscation()::username,
            buildScan.getObfuscation()::hostname,
            buildScan.getObfuscation()::ipAddresses
        );
        this.capture = new DefaultBuildScanCaptureAdapter(
            new Property<>(buildScan.getCapture()::setGoalInputFiles, buildScan.getCapture()::isGoalInputFiles),
            new Property<>(buildScan.getCapture()::setBuildLogging, buildScan.getCapture()::isBuildLogging),
            new Property<>(buildScan.getCapture()::setTestLogging, buildScan.getCapture()::isTestLogging)
        );
    }

    @Override
    public void tag(String tag) {
        buildScan.tag(tag);
    }

    @Override
    public void value(String name, String value) {
        buildScan.value(name, value);
    }

    @Override
    public void link(String name, String url) {
        buildScan.link(name, url);
    }

    @Override
    public void background(Consumer<? super BuildScanApiAdapter> action) {
        buildScan.background(__ -> action.accept(this));
    }

    @Override
    public void buildFinished(Consumer<? super BuildResultAdapter> action) {
        buildScan.buildFinished(result -> action.accept(new DefaultBuildResultAdapter(result.getFailures())));
    }

    @Override
    public void buildScanPublished(Consumer<? super PublishedBuildScanAdapter> action) {
        buildScan.buildScanPublished(scan -> action.accept(new DefaultPublishedBuildScanAdapter(scan.getBuildScanId(), scan.getBuildScanUri())));
    }

    @Override
    public void setTermsOfServiceUrl(String termsOfServiceUrl) {
        buildScan.setTermsOfServiceUrl(termsOfServiceUrl);
    }

    @Override
    public String getTermsOfServiceUrl() {
        return buildScan.getTermsOfServiceUrl();
    }

    @Override
    public void setTermsOfServiceAgree(String agree) {
        buildScan.setTermsOfServiceAgree(agree);
    }

    @Override
    public String getTermsOfServiceAgree() {
        return buildScan.getTermsOfServiceAgree();
    }

    @Override
    public void setServer(URI url) {
        buildScan.setServer(url);
    }

    @Override
    public String getServer() {
        return buildScan.getServer();
    }

    @Override
    public void setAllowUntrustedServer(boolean allow) {
        buildScan.setAllowUntrustedServer(allow);
    }

    @Override
    public boolean getAllowUntrustedServer() {
        return buildScan.getAllowUntrustedServer();
    }

    @Override
    public void publishAlways() {
        buildScan.publishAlways();
    }

    @Override
    public void publishAlwaysIf(boolean condition) {
        buildScan.publishAlwaysIf(condition);
    }

    @Override
    public void publishOnFailure() {
        buildScan.publishOnFailure();
    }

    @Override
    public void publishOnFailureIf(boolean condition) {
        buildScan.publishOnFailureIf(condition);
    }

    @Override
    public void publishOnDemand() {
        buildScan.publishOnDemand();
    }

    @Override
    public void setUploadInBackground(boolean uploadInBackground) {
        buildScan.setUploadInBackground(uploadInBackground);
    }

    @Override
    public boolean isUploadInBackground() {
        return buildScan.isUploadInBackground();
    }

    @Override
    public void executeOnce(String identifier, Consumer<? super BuildScanApiAdapter> action) {
        buildScan.executeOnce(identifier, __ -> action.accept(this));
    }

    @Override
    public BuildScanDataObfuscationAdapter getObfuscation() {
        return obfuscation;
    }

    @Override
    public BuildScanCaptureAdapter getCapture() {
        return capture;
    }
}
