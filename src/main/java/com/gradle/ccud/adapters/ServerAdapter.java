package com.gradle.ccud.adapters;

import java.net.URI;

/**
 * @see com.gradle.develocity.agent.maven.api.cache.Server
 * @see com.gradle.maven.extension.api.cache.Server
 */
public interface ServerAdapter {

    String getServerId();

    void setServerId(String serverId);

    URI getUrl();

    default void setUrl(String url) {
        this.setUrl(url == null ? null : URI.create(url));
    }

    void setUrl(URI url);

    boolean isAllowUntrusted();

    void setAllowUntrusted(boolean allowUntrusted);

    boolean isAllowInsecureProtocol();

    void setAllowInsecureProtocol(boolean allowInsecureProtocol);

    boolean isUseExpectContinue();

    void setUseExpectContinue(boolean useExpectContinue);

    CredentialsAdapter getCredentials();

}
