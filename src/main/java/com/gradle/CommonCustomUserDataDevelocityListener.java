package com.gradle;

import com.gradle.develocity.agent.maven.adapters.develocity.DevelocityApiAdapter;
import com.gradle.develocity.agent.maven.api.DevelocityApi;
import com.gradle.develocity.agent.maven.api.DevelocityListener;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.eclipse.sisu.Description;

import javax.inject.Named;
import javax.inject.Singleton;

@Named("common-custom-user-data-develocity-listener")
@Singleton
@Description("Captures common custom user data in Maven Build Scan")
public final class CommonCustomUserDataDevelocityListener extends CommonCustomUserDataListener implements DevelocityListener {

    @Override
    public void configure(DevelocityApi api, MavenSession session) throws MavenExecutionException {
        super.configure(new DevelocityApiAdapter(api), session, CustomConfigurationSpec.DEVELOCITY);
    }

}
