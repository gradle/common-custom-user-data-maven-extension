package com.gradle;

import com.gradle.maven.extension.api.scan.BuildScanApi;
import org.apache.maven.execution.MavenSession;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static com.gradle.CiUtils.isAzurePipelines;
import static com.gradle.CiUtils.isBamboo;
import static com.gradle.CiUtils.isBitrise;
import static com.gradle.CiUtils.isCi;
import static com.gradle.CiUtils.isCircleCI;
import static com.gradle.CiUtils.isGitHubActions;
import static com.gradle.CiUtils.isGitLab;
import static com.gradle.CiUtils.isGoCD;
import static com.gradle.CiUtils.isHudson;
import static com.gradle.CiUtils.isJenkins;
import static com.gradle.CiUtils.isTeamCity;
import static com.gradle.CiUtils.isTravis;
import static com.gradle.Utils.appendIfMissing;
import static com.gradle.Utils.envVariable;
import static com.gradle.Utils.execAndCheckSuccess;
import static com.gradle.Utils.execAndGetStdOut;
import static com.gradle.Utils.isNotEmpty;
import static com.gradle.Utils.projectProperty;
import static com.gradle.Utils.readPropertiesFile;
import static com.gradle.Utils.redactUserInfo;
import static com.gradle.Utils.sysProperty;
import static com.gradle.Utils.urlEncode;

/**
 * Adds a standard set of useful tags, links and custom values to all build scans published.
 */
final class CustomBuildScanEnhancements {

    private static final String SYSTEM_PROP_IDEA_VENDOR_NAME = "idea.vendor.name";
    private static final String SYSTEM_PROP_IDEA_VERSION = "idea.version";
    private static final String SYSTEM_PROP_ECLIPSE_BUILD_ID = "eclipse.buildId";
    private static final String SYSTEM_PROP_IDEA_SYNC_ACTIVE = "idea.sync.active";

    private final BuildScanApi buildScan;
    private final MavenSession mavenSession;

    CustomBuildScanEnhancements(BuildScanApi buildScan, MavenSession mavenSession) {
        this.buildScan = buildScan;
        this.mavenSession = mavenSession;
    }

    void apply() {
        captureOs();
        captureIde();
        captureCiOrLocal();
        captureCiMetadata();
        captureGitMetadata();
        captureSkipTestsFlags();
    }

    private void captureOs() {
        sysProperty("os.name").ifPresent(buildScan::tag);
    }

    private void captureIde() {
        if (!isCi()) {
            Map<String, Optional<String>> ideProperties = new HashMap<>();
            ideProperties.put(SYSTEM_PROP_IDEA_VENDOR_NAME, sysProperty(SYSTEM_PROP_IDEA_VENDOR_NAME));
            ideProperties.put(SYSTEM_PROP_IDEA_VERSION, sysProperty(SYSTEM_PROP_IDEA_VERSION));
            ideProperties.put(SYSTEM_PROP_ECLIPSE_BUILD_ID, sysProperty(SYSTEM_PROP_ECLIPSE_BUILD_ID));
            ideProperties.put(SYSTEM_PROP_IDEA_SYNC_ACTIVE, sysProperty(SYSTEM_PROP_IDEA_SYNC_ACTIVE));

            new CaptureIdeMetadataAction(buildScan, ideProperties).execute();
        }
    }

    private static final class CaptureIdeMetadataAction {

        private final BuildScanApi buildScan;
        private final Map<String, Optional<String>> props;

        private CaptureIdeMetadataAction(BuildScanApi buildScan, Map<String, Optional<String>> props) {
            this.buildScan = buildScan;
            this.props = props;
        }

        private void execute() {
            if (props.get(SYSTEM_PROP_IDEA_VENDOR_NAME).isPresent()) {
                String ideaVendorNameValue = props.get(SYSTEM_PROP_IDEA_VENDOR_NAME).get();
                if (ideaVendorNameValue.equals("JetBrains")) {
                    tagIde("IntelliJ IDEA", props.get(SYSTEM_PROP_IDEA_VERSION).orElse(""));
                }
            } else if (props.get(SYSTEM_PROP_IDEA_VERSION).isPresent()) {
                // this case should be handled by the ideaVendorName condition but keeping it for compatibility reason (ideaVendorName started with 2020.1)
                tagIde("IntelliJ IDEA", props.get(SYSTEM_PROP_IDEA_VERSION).get());
            } else if (props.get(SYSTEM_PROP_ECLIPSE_BUILD_ID).isPresent()) {
                tagIde("Eclipse", props.get(SYSTEM_PROP_ECLIPSE_BUILD_ID).get());
            } else {
                buildScan.tag("Cmd Line");
            }

            if (props.get(SYSTEM_PROP_IDEA_SYNC_ACTIVE).isPresent()) {
                buildScan.tag("IDE sync");
            }
        }

        private void tagIde(String ideLabel, String version) {
            buildScan.tag(ideLabel);
            if (!version.isEmpty()) {
                buildScan.value(ideLabel + " version", version);
            }
        }

    }

    private void captureCiOrLocal() {
        buildScan.tag(isCi() ? "CI" : "LOCAL");
    }

    private void captureCiMetadata() {
        if (isCi()) {
            new CaptureCiMetadataAction(buildScan).execute();
        }
    }

    private static final class CaptureCiMetadataAction {
        private final BuildScanApi buildScan;

        public CaptureCiMetadataAction(BuildScanApi buildScan) {
            this.buildScan = buildScan;
        }

        private void execute() {
            if (isJenkins() || isHudson()) {
                Optional<String> buildUrl = envVariable("BUILD_URL");
                Optional<String> buildNumber = envVariable("BUILD_NUMBER");
                Optional<String> nodeName = envVariable("NODE_NAME");
                Optional<String> jobName = envVariable("JOB_NAME");
                Optional<String> stageName = envVariable("STAGE_NAME");

                buildUrl.ifPresent(url ->
                        buildScan.link(isJenkins() ? "Jenkins build" : "Hudson build", url));
                buildNumber.ifPresent(value ->
                        buildScan.value("CI build number", value));
                nodeName.ifPresent(value ->
                        addCustomValueAndSearchLink(buildScan, "CI node", value));
                jobName.ifPresent(value ->
                        addCustomValueAndSearchLink(buildScan, "CI job", value));
                stageName.ifPresent(value ->
                        addCustomValueAndSearchLink(buildScan, "CI stage", value));

                jobName.ifPresent(j -> buildNumber.ifPresent(b -> {
                    Map<String, String> params = new LinkedHashMap<>();
                    params.put("CI job", j);
                    params.put("CI build number", b);
                    addSearchLink(buildScan, "CI pipeline", params);
                }));
            }

            if (isTeamCity()) {
                Optional<String> teamcityBuildPropertiesFile = envVariable("TEAMCITY_BUILD_PROPERTIES_FILE");
                if (teamcityBuildPropertiesFile.isPresent()) {
                    Properties buildProperties = readPropertiesFile(teamcityBuildPropertiesFile.get());

                    String teamCityBuildId = buildProperties.getProperty("teamcity.build.id");
                    if (isNotEmpty(teamCityBuildId)) {
                        String teamcityConfigFile = buildProperties.getProperty("teamcity.configuration.properties.file");
                        if (isNotEmpty(teamcityConfigFile)) {
                            Properties configProperties = readPropertiesFile(teamcityConfigFile);

                            String teamCityServerUrl = configProperties.getProperty("teamcity.serverUrl");
                            if (isNotEmpty(teamCityServerUrl)) {
                                String buildUrl = appendIfMissing(teamCityServerUrl, "/") + "viewLog.html?buildId=" + urlEncode(teamCityBuildId);
                                buildScan.link("TeamCity build", buildUrl);
                            }
                        }
                    }

                    String teamCityBuildNumber = buildProperties.getProperty("build.number");
                    if (isNotEmpty(teamCityBuildNumber)) {
                        buildScan.value("CI build number", teamCityBuildNumber);
                    }
                    String teamCityBuildTypeId = buildProperties.getProperty("teamcity.buildType.id");
                    if (isNotEmpty(teamCityBuildTypeId)) {
                        addCustomValueAndSearchLink(buildScan, "CI build config", teamCityBuildTypeId);
                    }
                    String teamCityAgentName = buildProperties.getProperty("agent.name");
                    if (isNotEmpty(teamCityAgentName)) {
                        addCustomValueAndSearchLink(buildScan, "CI agent", teamCityAgentName);
                    }
                }
            }

            if (isCircleCI()) {
                envVariable("CIRCLE_BUILD_URL").ifPresent(url ->
                        buildScan.link("CircleCI build", url));
                envVariable("CIRCLE_BUILD_NUM").ifPresent(value ->
                        buildScan.value("CI build number", value));
                envVariable("CIRCLE_JOB").ifPresent(value ->
                        addCustomValueAndSearchLink(buildScan, "CI job", value));
                envVariable("CIRCLE_WORKFLOW_ID").ifPresent(value ->
                        addCustomValueAndSearchLink(buildScan, "CI workflow", value));
            }

            if (isBamboo()) {
                envVariable("bamboo_resultsUrl").ifPresent(url ->
                        buildScan.link("Bamboo build", url));
                envVariable("bamboo_buildNumber").ifPresent(value ->
                        buildScan.value("CI build number", value));
                envVariable("bamboo_planName").ifPresent(value ->
                        addCustomValueAndSearchLink(buildScan, "CI plan", value));
                envVariable("bamboo_buildPlanName").ifPresent(value ->
                        addCustomValueAndSearchLink(buildScan, "CI build plan", value));
                envVariable("bamboo_agentId").ifPresent(value ->
                        addCustomValueAndSearchLink(buildScan, "CI agent", value));
            }

            if (isGitHubActions()) {
                Optional<String> gitHubRepository = envVariable("GITHUB_REPOSITORY");
                Optional<String> gitHubRunId = envVariable("GITHUB_RUN_ID");
                if (gitHubRepository.isPresent() && gitHubRunId.isPresent()) {
                    buildScan.link("GitHub Actions build", "https://github.com/" + gitHubRepository.get() + "/actions/runs/" + gitHubRunId.get());
                }
                envVariable("GITHUB_WORKFLOW").ifPresent(value ->
                        addCustomValueAndSearchLink(buildScan, "CI workflow", value));
                envVariable("GITHUB_RUN_ID").ifPresent(value ->
                        addCustomValueAndSearchLink(buildScan, "CI run", value));
            }

            if (isGitLab()) {
                envVariable("CI_JOB_URL").ifPresent(url ->
                        buildScan.link("GitLab build", url));
                envVariable("CI_PIPELINE_URL").ifPresent(url ->
                        buildScan.link("GitLab pipeline", url));
                envVariable("CI_JOB_NAME").ifPresent(value ->
                        addCustomValueAndSearchLink(buildScan, "CI job", value));
                envVariable("CI_JOB_STAGE").ifPresent(value ->
                        addCustomValueAndSearchLink(buildScan, "CI stage", value));
            }

            if (isTravis()) {
                envVariable("TRAVIS_BUILD_WEB_URL").ifPresent(url ->
                        buildScan.link("Travis build", url));
                envVariable("TRAVIS_BUILD_NUMBER").ifPresent(value ->
                        buildScan.value("CI build number", value));
                envVariable("TRAVIS_JOB_NAME").ifPresent(value ->
                        addCustomValueAndSearchLink(buildScan, "CI job", value));
                envVariable("TRAVIS_EVENT_TYPE").ifPresent(buildScan::tag);
            }

            if (isBitrise()) {
                envVariable("BITRISE_BUILD_URL").ifPresent(url ->
                        buildScan.link("Bitrise build", url));
                envVariable("BITRISE_BUILD_NUMBER").ifPresent(value ->
                        buildScan.value("CI build number", value));
            }

            if (isGoCD()) {
                Optional<String> pipelineName = envVariable("GO_PIPELINE_NAME");
                Optional<String> pipelineNumber = envVariable("GO_PIPELINE_COUNTER");
                Optional<String> stageName = envVariable("GO_STAGE_NAME");
                Optional<String> stageNumber = envVariable("GO_STAGE_COUNTER");
                Optional<String> jobName = envVariable("GO_JOB_NAME");
                Optional<String> goServerUrl = envVariable("GO_SERVER_URL");
                if (Stream.of(pipelineName, pipelineNumber, stageName, stageNumber, jobName, goServerUrl).allMatch(Optional::isPresent)) {
                    //noinspection OptionalGetWithoutIsPresent
                    String buildUrl = String.format("%s/tab/build/detail/%s/%s/%s/%s/%s",
                            goServerUrl.get(), pipelineName.get(),
                            pipelineNumber.get(), stageName.get(), stageNumber.get(), jobName.get());
                    buildScan.link("GoCD build", buildUrl);
                } else if (goServerUrl.isPresent()) {
                    buildScan.link("GoCD", goServerUrl.get());
                }
                pipelineName.ifPresent(value ->
                        addCustomValueAndSearchLink(buildScan, "CI pipeline", value));
                jobName.ifPresent(value ->
                        addCustomValueAndSearchLink(buildScan, "CI job", value));
                stageName.ifPresent(value ->
                        addCustomValueAndSearchLink(buildScan, "CI stage", value));
            }

            if (isAzurePipelines()) {
                Optional<String> azureServerUrl = envVariable("SYSTEM_TEAMFOUNDATIONCOLLECTIONURI");
                Optional<String> azureProject = envVariable("SYSTEM_TEAMPROJECT");
                Optional<String> buildId = envVariable("BUILD_BUILDID");
                if (Stream.of(azureServerUrl, azureProject, buildId).allMatch(Optional::isPresent)) {
                    //noinspection OptionalGetWithoutIsPresent
                    String buildUrl = String.format("%s%s/_build/results?buildId=%s",
                            azureServerUrl.get(), azureProject.get(), buildId.get());
                    buildScan.link("Azure Pipelines build", buildUrl);
                } else if (azureServerUrl.isPresent()) {
                    buildScan.link("Azure Pipelines", azureServerUrl.get());
                }

                buildId.ifPresent(value ->
                        buildScan.value("CI build number", value));
            }
        }
    }

    private void captureGitMetadata() {
        // Run expensive computation in background
        buildScan.background(new CaptureGitMetadataAction());
    }

    private static final class CaptureGitMetadataAction implements Consumer<BuildScanApi> {

        @Override
        public void accept(BuildScanApi buildScan) {
            if (!isGitInstalled()) {
                return;
            }

            String gitRepo = execAndGetStdOut("git", "config", "--get", "remote.origin.url");
            String gitCommitId = execAndGetStdOut("git", "rev-parse", "--verify", "HEAD");
            String gitCommitShortId = execAndGetStdOut("git", "rev-parse", "--short=8", "--verify", "HEAD");
            String gitBranchName = getGitBranchName(() -> execAndGetStdOut("git", "rev-parse", "--abbrev-ref", "HEAD"));
            String gitStatus = execAndGetStdOut("git", "status", "--porcelain");

            if (isNotEmpty(gitRepo)) {
                buildScan.value("Git repository", redactUserInfo(gitRepo));
            }
            if (isNotEmpty(gitCommitId)) {
                buildScan.value("Git commit id", gitCommitId);
            }
            if (isNotEmpty(gitCommitShortId)) {
                addCustomValueAndSearchLink(buildScan, "Git commit id", "Git commit id short", gitCommitShortId);
            }
            if (isNotEmpty(gitBranchName)) {
                buildScan.tag(gitBranchName);
                buildScan.value("Git branch", gitBranchName);
            }
            if (isNotEmpty(gitStatus)) {
                buildScan.tag("Dirty");
                buildScan.value("Git status", gitStatus);
            }

            if (isNotEmpty(gitRepo) && isNotEmpty(gitCommitId)) {
                if (gitRepo.contains("github.com/") || gitRepo.contains("github.com:")) {
                    Matcher matcher = Pattern.compile("(.*)github\\.com[/|:](.*)").matcher(gitRepo);
                    if (matcher.matches()) {
                        String rawRepoPath = matcher.group(2);
                        String repoPath = rawRepoPath.endsWith(".git") ? rawRepoPath.substring(0, rawRepoPath.length() - 4) : rawRepoPath;
                        buildScan.link("Github source", "https://github.com/" + repoPath + "/tree/" + gitCommitId);
                    }
                } else if (gitRepo.contains("gitlab.com/") || gitRepo.contains("gitlab.com:")) {
                    Matcher matcher = Pattern.compile("(.*)gitlab\\.com[/|:](.*)").matcher(gitRepo);
                    if (matcher.matches()) {
                        String rawRepoPath = matcher.group(2);
                        String repoPath = rawRepoPath.endsWith(".git") ? rawRepoPath.substring(0, rawRepoPath.length() - 4) : rawRepoPath;
                        buildScan.link("GitLab Source", "https://gitlab.com/" + repoPath + "/-/commit/" + gitCommitId);
                    }
                }
            }
        }

        private boolean isGitInstalled() {
            return execAndCheckSuccess("git", "--version");
        }

        private String getGitBranchName(Supplier<String> gitCommand) {
            if (isJenkins() || isHudson()) {
                Optional<String> branch = envVariable("BRANCH_NAME");
                if (branch.isPresent()) {
                    return branch.get();
                }
            } else if (isGitLab()) {
                Optional<String> branch = envVariable("CI_COMMIT_REF_NAME");
                if (branch.isPresent()) {
                    return branch.get();
                }
            } else if (isAzurePipelines()){
                Optional<String> branch = envVariable("BUILD_SOURCEBRANCH");
                if (branch.isPresent()) {
                    return branch.get();
                }
            }
            return gitCommand.get();
        }

    }

    private void captureSkipTestsFlags() {
        addCustomValueWhenProjectPropertyResolvesToTrue("skipITs");
        addCustomValueWhenProjectPropertyResolvesToTrue("skipTests");
        addCustomValueWhenProjectPropertyResolvesToTrue("maven.test.skip");
    }

    private void addCustomValueWhenProjectPropertyResolvesToTrue(String property) {
        projectProperty(mavenSession, property).ifPresent(value -> {
            if (value.isEmpty() || Boolean.valueOf(value).equals(Boolean.TRUE)) {
                buildScan.value("switches." + property, "On");
            }
        });
    }

    private static void addCustomValueAndSearchLink(BuildScanApi buildScan, String name, String value) {
        addCustomValueAndSearchLink(buildScan, name, name, value);
    }

    private static void addCustomValueAndSearchLink(BuildScanApi buildScan, String linkLabel, String name, String value) {
        // Set custom values immediately, but do not add custom links until 'buildFinished' since
        // creating customs links requires the server url to be fully configured
        buildScan.value(name, value);
        buildScan.buildFinished(result -> addSearchLink(buildScan, linkLabel, name, value));
    }

    private static void addSearchLink(BuildScanApi buildScan, String linkLabel, Map<String, String> values) {
        // the parameters for a link querying multiple custom values look like:
        // search.names=name1,name2&search.values=value1,value2
        // this reduction groups all names and all values together in order to properly generate the query
        values.entrySet().stream()
            .sorted(Map.Entry.comparingByKey()) // results in a deterministic order of link parameters
            .reduce((a, b) -> new AbstractMap.SimpleEntry<>(a.getKey() + "," + b.getKey(), a.getValue() + "," + b.getValue()))
            .ifPresent(x -> buildScan.buildFinished(result -> addSearchLink(buildScan, linkLabel, x.getKey(), x.getValue())));
    }

    private static void addSearchLink(BuildScanApi buildScan, String linkLabel, String name, String value) {
        String server = buildScan.getServer();
        if (server != null) {
            String searchParams = "search.names=" + urlEncode(name) + "&search.values=" + urlEncode(value);
            String url = appendIfMissing(server, "/") + "scans?" + searchParams + "#selection.buildScanB=" + urlEncode("{SCAN_ID}");
            buildScan.link(linkLabel + " build scans", url);
        }
    }

}
