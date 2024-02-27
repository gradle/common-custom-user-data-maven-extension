package com.gradle;

import com.gradle.ccud.adapters.DevelocityAdapter;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.slf4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.List;

final class GroovyScriptUserData {

    static void evaluate(MavenSession session, DevelocityAdapter develocity, Logger logger) throws MavenExecutionException {
        List<File> groovyScripts = getGroovyScripts(session);
        for (File script : groovyScripts) {
            if (script.exists()) {
                logger.debug("Evaluating custom user data Groovy script: " + script);
                evaluateGroovyScript(session, develocity, logger, script);
            } else {
                logger.debug("Skipping evaluation of custom user data Groovy script because it does not exist: " + script);
            }
        }
    }

    private static List<File> getGroovyScripts(MavenSession session) {
        File rootDir = session.getRequest().getMultiModuleProjectDirectory();
        return Arrays.asList(
            new File(rootDir, ".mvn/gradle-enterprise-custom-user-data.groovy"),
            new File(rootDir, ".mvn/develocity-custom-user-data.groovy")
        );
    }

    private static void evaluateGroovyScript(MavenSession session, DevelocityAdapter develocity, Logger logger, File groovyScript) throws MavenExecutionException {
        try {
            Binding binding = prepareBinding(session, develocity, logger);
            new GroovyShell(GroovyScriptUserData.class.getClassLoader(), binding).evaluate(groovyScript);
        } catch (Exception e) {
            throw new MavenExecutionException("Failed to evaluate custom user data Groovy script: " + groovyScript, e);
        }
    }

    private static Binding prepareBinding(MavenSession session, DevelocityAdapter develocity, Logger logger) {
        Binding binding = new Binding();
        binding.setVariable("project", session.getTopLevelProject());
        binding.setVariable("session", session);
        binding.setVariable("develocity", develocity);
        binding.setVariable("gradleEnterprise", develocity);
        binding.setVariable("buildScan", develocity.getBuildScan());
        binding.setVariable("buildCache", develocity.getBuildCache());
        binding.setVariable("log", logger);
        return binding;
    }

    private GroovyScriptUserData() {
    }

}
