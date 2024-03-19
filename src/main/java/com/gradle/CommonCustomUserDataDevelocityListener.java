package com.gradle;

import com.gradle.develocity.agent.maven.adapters.develocity.DevelocityApiAdapter;
import com.gradle.develocity.agent.maven.api.DevelocityApi;
import com.gradle.develocity.agent.maven.api.DevelocityListener;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;

@SuppressWarnings("unused")
@Component(
    role = DevelocityListener.class,
    hint = "common-custom-user-data-develocity-listener",
    description = "Captures common custom user data in Maven build scans"
)
public final class CommonCustomUserDataDevelocityListener extends CommonCustomUserDataListener implements DevelocityListener {

    @Override
    public void configure(DevelocityApi api, MavenSession session) throws MavenExecutionException {
        super.configure(new DevelocityApiAdapter(api), session, CustomConfigurationSpec.DEVELOCITY);
    }

}
