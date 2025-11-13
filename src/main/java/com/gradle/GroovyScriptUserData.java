package com.gradle;

import com.gradle.develocity.agent.maven.adapters.DevelocityAdapter;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.apache.maven.MavenExecutionException;
import org.apache.maven.execution.MavenSession;
import org.slf4j.Logger;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

final class GroovyScriptUserData {

    static void evaluate(MavenSession session, DevelocityAdapter develocity, Logger logger, CustomConfigurationSpec customConfigurationSpec) throws MavenExecutionException {
        evaluateGroovyScriptInRootProject(session, develocity, logger, customConfigurationSpec);
        evaluateGroovyScriptsInDevelocityStorageDirectory(session, develocity, logger, customConfigurationSpec);
    }

    private static void evaluateGroovyScriptInRootProject(MavenSession session, DevelocityAdapter develocity, Logger logger, CustomConfigurationSpec customConfigurationSpec) throws MavenExecutionException {
        File script = getGroovyScript(session, customConfigurationSpec.groovyScriptName);
        if (script.exists()) {
            logger.debug("Evaluating custom user data Groovy script: {}", script);
            evaluateGroovyScript(session, develocity, logger, script, customConfigurationSpec.apiVariableName);
        } else if (!customConfigurationSpec.fallbackScript.isPresent()) {
            logger.debug("Skipping evaluation of custom user data Groovy script because it does not exist: " + script);
        } else {
            CustomConfigurationSpec fallbackSpec = customConfigurationSpec.fallbackScript.get();
            File fallbackScript = getGroovyScript(session, fallbackSpec.groovyScriptName);
            if (fallbackScript.exists()) {
                logger.warn("Evaluating deprecated custom user data Groovy script: {}. Use '{}.groovy' scripts instead.", fallbackScript, customConfigurationSpec.groovyScriptName);
                evaluateGroovyScript(session, develocity, logger, fallbackScript, fallbackSpec.apiVariableName);
            } else {
                logger.debug("Skipping evaluation of custom user data Groovy script because it does not exist: " + fallbackScript);
            }
        }
    }

    private static void evaluateGroovyScriptsInDevelocityStorageDirectory(MavenSession session, DevelocityAdapter develocity, Logger logger, CustomConfigurationSpec customConfigurationSpec) throws MavenExecutionException {
        File customUserDataDirectory = develocity.getStorageDirectory().resolve("custom-user-data").toFile();
        List<File> scripts = getGroovyScripts(customUserDataDirectory);
        for (File script : scripts) {
            logger.debug("Evaluating custom user data Groovy script: {}", script);
            evaluateGroovyScript(session, develocity, logger, script, customConfigurationSpec.apiVariableName);
        }
    }

    private static File getGroovyScript(MavenSession session, String scriptName) {
        File rootDir = session.getRequest().getMultiModuleProjectDirectory();
        return new File(rootDir, ".mvn/" + scriptName + ".groovy");
    }

    private static List<File> getGroovyScripts(File directory) {
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                return Arrays.stream(files)
                        .filter(GroovyScriptUserData::isGroovyScript)
                        .sorted(Comparator.comparing(File::getName))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    private static boolean isGroovyScript(File file) {
        return file.isFile() && file.getName().endsWith(".groovy");
    }

    private static void evaluateGroovyScript(MavenSession session, DevelocityAdapter develocity, Logger logger, File groovyScript, String apiVariableName) throws MavenExecutionException {
        try {
            Binding binding = prepareBinding(session, develocity, logger, apiVariableName);
            new GroovyShell(GroovyScriptUserData.class.getClassLoader(), binding).evaluate(groovyScript);
        } catch (Exception e) {
            throw new MavenExecutionException("Failed to evaluate custom user data Groovy script: " + groovyScript, e);
        }
    }

    private static Binding prepareBinding(MavenSession session, DevelocityAdapter develocity, Logger logger, String apiVariableName) {
        Binding binding = new Binding();
        binding.setVariable("project", session.getTopLevelProject());
        binding.setVariable("session", session);
        binding.setVariable(apiVariableName, develocity);
        binding.setVariable("buildScan", develocity.getBuildScan());
        binding.setVariable("buildCache", develocity.getBuildCache());
        binding.setVariable("log", logger);
        return binding;
    }

    private GroovyScriptUserData() {
    }

}
