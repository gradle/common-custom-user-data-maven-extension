package com.gradle;

import com.gradle.develocity.agent.maven.adapters.enterprise.GradleEnterpriseApiAdapter;
import com.gradle.maven.extension.api.GradleEnterpriseApi;
import com.gradle.maven.extension.api.GradleEnterpriseListener;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;

public final class CommonCustomUserDataGradleEnterpriseListener extends CommonCustomUserDataListener implements GradleEnterpriseListener {

    @Override
    public void configure(GradleEnterpriseApi api, MavenSession session) throws MavenExecutionException {
        super.configure(new GradleEnterpriseApiAdapter(api), session, CustomConfigurationSpec.GRADLE_ENTERPRISE);
    }

}
