package com.gradle.ccud.adapters;

import java.net.URI;
import java.util.function.Consumer;

/**
 * @see com.gradle.develocity.agent.maven.api.scan.BuildScanApi
 * @see com.gradle.maven.extension.api.scan.BuildScanApi
 */
public interface BuildScanApiAdapter {

    void tag(String tag);

    void value(String name, String value);

    void link(String name, String url);

    void background(Consumer<? super BuildScanApiAdapter> action);

    void buildFinished(Consumer<? super BuildResultAdapter> action);

    void buildScanPublished(Consumer<? super PublishedBuildScanAdapter> action);

    void setTermsOfUseUrl(String termsOfServiceUrl);

    String getTermsOfUseUrl();

    void setTermsOfUseAgree(String agree);

    String getTermsOfUseAgree();

    default void setServer(String url) {
        setServer(url == null ? null : URI.create(url));
    }

    void setServer(URI url);

    String getServer();

    void setAllowUntrustedServer(boolean allow);

    boolean getAllowUntrustedServer();

    void publishAlways();

    void publishAlwaysIf(boolean condition);

    void publishOnFailure();

    void publishOnFailureIf(boolean condition);

    void publishOnDemand();

    void setUploadInBackground(boolean uploadInBackground);

    boolean isUploadInBackground();

    void executeOnce(String identifier, Consumer<? super BuildScanApiAdapter> action);

    BuildScanDataObfuscationAdapter getObfuscation();

    default void obfuscation(Consumer<? super BuildScanDataObfuscationAdapter> action) {
        action.accept(getObfuscation());
    }

    BuildScanCaptureAdapter getCapture();

    default void capture(Consumer<? super BuildScanCaptureAdapter> action) {
        action.accept(getCapture());
    }

}
