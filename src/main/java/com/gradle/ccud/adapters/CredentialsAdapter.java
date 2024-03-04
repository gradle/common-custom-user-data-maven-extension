package com.gradle.ccud.adapters;

/**
 * @see com.gradle.develocity.agent.maven.api.cache.Credentials
 * @see com.gradle.maven.extension.api.cache.Credentials
 */
public interface CredentialsAdapter {

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

}
