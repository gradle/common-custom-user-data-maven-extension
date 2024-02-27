package com.gradle.ccud.adapters.shared;

import com.gradle.ccud.adapters.CredentialsAdapter;
import com.gradle.ccud.adapters.Property;
import com.gradle.ccud.adapters.ServerAdapter;

import java.net.URI;

public class DefaultServerAdapter implements ServerAdapter {

    private final Property<String> serverId;
    private final Property<URI> url;
    private final Property<Boolean> allowUntrusted;
    private final Property<Boolean> allowInsecureProtocol;
    private final Property<Boolean> useExpectContinue;
    private final CredentialsAdapter credentialsAdapter;

    public DefaultServerAdapter(
        Property<String> serverId,
        Property<URI> url,
        Property<Boolean> allowUntrusted,
        Property<Boolean> allowInsecureProtocol,
        Property<Boolean> useExpectContinue,
        CredentialsAdapter credentialsAdapter
    ) {
        this.serverId = serverId;
        this.url = url;
        this.allowUntrusted = allowUntrusted;
        this.allowInsecureProtocol = allowInsecureProtocol;
        this.useExpectContinue = useExpectContinue;
        this.credentialsAdapter = credentialsAdapter;
    }

    @Override
    public String getServerId() {
        return serverId.get();
    }

    @Override
    public void setServerId(String serverId) {
        this.serverId.set(serverId);
    }

    @Override
    public URI getUrl() {
        return url.get();
    }

    @Override
    public void setUrl(URI url) {
        this.url.set(url);
    }

    @Override
    public boolean isAllowUntrusted() {
        return allowUntrusted.get();
    }

    @Override
    public void setAllowUntrusted(boolean allowUntrusted) {
        this.allowUntrusted.set(allowUntrusted);
    }

    @Override
    public boolean isAllowInsecureProtocol() {
        return allowInsecureProtocol.get();
    }

    @Override
    public void setAllowInsecureProtocol(boolean allowInsecureProtocol) {
        this.allowInsecureProtocol.set(allowInsecureProtocol);
    }

    @Override
    public boolean isUseExpectContinue() {
        return useExpectContinue.get();
    }

    @Override
    public void setUseExpectContinue(boolean useExpectContinue) {
        this.useExpectContinue.set(useExpectContinue);
    }

    @Override
    public CredentialsAdapter getCredentials() {
        return credentialsAdapter;
    }
}
