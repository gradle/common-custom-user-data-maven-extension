package com.gradle;

final class CiUtils {

    private CiUtils() {
    }

    static boolean isCi() {
        return isGenericCI()
                || isJenkins()
                || isHudson()
                || isTeamCity()
                || isCircleCI()
                || isBamboo()
                || isGitHubActions()
                || isGitLab()
                || isTravis()
                || isBitrise()
                || isGoCD()
                || isAzurePipelines();
    }

    static boolean isGenericCI() {
        return Utils.envVariable("CI").isPresent()
                || Utils.sysProperty("CI").isPresent();
    }

    static boolean isJenkins() {
        return Utils.envVariable("JENKINS_URL").isPresent();
    }

    static boolean isHudson() {
        return Utils.envVariable("HUDSON_URL").isPresent();
    }

    static boolean isTeamCity() {
        return Utils.envVariable("TEAMCITY_VERSION").isPresent();
    }

    static boolean isCircleCI() {
        return Utils.envVariable("CIRCLE_BUILD_URL").isPresent();
    }

    static boolean isBamboo() {
        return Utils.envVariable("bamboo_resultsUrl").isPresent();
    }

    static boolean isGitHubActions() {
        return Utils.envVariable("GITHUB_ACTIONS").isPresent();
    }

    static boolean isGitLab() {
        return Utils.envVariable("GITLAB_CI").isPresent();
    }

    static boolean isTravis() {
        return Utils.envVariable("TRAVIS_JOB_ID").isPresent();
    }

    static boolean isBitrise() {
        return Utils.envVariable("BITRISE_BUILD_URL").isPresent();
    }

    static boolean isGoCD() {
        return Utils.envVariable("GO_SERVER_URL").isPresent();
    }

    static boolean isAzurePipelines() {
        return Utils.envVariable("TF_BUILD").isPresent();
    }

}
