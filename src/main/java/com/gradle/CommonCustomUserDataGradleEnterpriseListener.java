package com.gradle;

import com.gradle.develocity.agent.maven.adapters.enterprise.GradleEnterpriseApiAdapter;
import com.gradle.maven.extension.api.GradleEnterpriseApi;
import com.gradle.maven.extension.api.GradleEnterpriseListener;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.codehaus.plexus.component.annotations.Component;

@SuppressWarnings("unused")
@Component(
    role = GradleEnterpriseListener.class,
    hint = "common-custom-user-data-gradle-enterprise-listener",
    description = "Captures common custom user data in Maven build scans"
)
public final class CommonCustomUserDataGradleEnterpriseListener extends CommonCustomUserDataListener implements GradleEnterpriseListener {

    @Override
    public void configure(GradleEnterpriseApi api, MavenSession session) throws MavenExecutionException {
        super.configure(new GradleEnterpriseApiAdapter(api), session, CustomConfigurationSpec.GRADLE_ENTERPRISE);
    }

}
