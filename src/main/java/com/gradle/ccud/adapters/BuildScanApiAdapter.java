package com.gradle.ccud.adapters;

import java.net.URI;
import java.util.function.Consumer;

public interface BuildScanApiAdapter {

    void tag(String tag);

    void value(String name, String value);

    void link(String name, String url);

    void background(Consumer<? super BuildScanApiAdapter> action);

    void buildFinished(Consumer<? super BuildResultAdapter> action);

    void buildScanPublished(Consumer<? super PublishedBuildScanAdapter> action);

    void setTermsOfServiceUrl(String termsOfServiceUrl);

    String getTermsOfServiceUrl();

    void setTermsOfServiceAgree(String agree);

    String getTermsOfServiceAgree();

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

    void capture(Consumer<? super BuildScanCaptureAdapter> action);

}
