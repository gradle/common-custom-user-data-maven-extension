> _This repository is maintained by the Develocity Solutions team, as one of several publicly available repositories:_
> - _[Develocity Build Configuration Samples][develocity-build-config-samples]_
> - _[Develocity Build Validation Scripts][develocity-build-validation-scripts]_
> - _[Develocity Open Source Projects][develocity-oss-projects]_
> - _[Common Custom User Data Maven Extension][ccud-maven-extension] (this repository)_
> - _[Common Custom User Data Gradle Plugin][ccud-gradle-plugin]_
> - _[Common Custom User Data sbt Plugin][ccud-sbt-plugin]_ 
> - _[Android Cache Fix Gradle Plugin][android-cache-fix-plugin]_

# Common Custom User Data Maven Extension

[![Verify Build](https://github.com/gradle/common-custom-user-data-maven-extension/actions/workflows/build-verification.yml/badge.svg?branch=main)](https://github.com/gradle/common-custom-user-data-maven-extension/actions/workflows/build-verification.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.gradle/common-custom-user-data-maven-extension)](https://search.maven.org/artifact/com.gradle/common-custom-user-data-maven-extension)
[![Revved up by Develocity](https://img.shields.io/badge/Revved%20up%20by-Develocity-06A0CE?logo=Gradle&labelColor=02303A)](https://ge.solutions-team.gradle.com/scans)

The Common Custom User Data Maven extension for Develocity enhances published build scans
by adding a set of tags, links and custom values that have proven to be useful for many projects building with Develocity.

You can leverage this extension for your project in one of two ways:
1. [Apply the published extension](#applying-the-published-extension) directly in your `.mvn/extensions.xml` and immediately benefit from enhanced build scans
2. Copy this repository and [develop a customized version of the extension](#developing-a-customized-version-of-the-extension) to standardize Develocity usage across multiple projects

## Applying the published extension

The Common Custom User Data Maven extension is available in [Maven Central](https://search.maven.org/artifact/com.gradle/common-custom-user-data-maven-extension). This extension
requires the [Develocity Maven extension](https://search.maven.org/artifact/com.gradle/develocity-maven-extension) to also be applied in your build in order to have
an effect.

In order for the Common Custom User Data Maven extension to become active, you need to register it in the `.mvn/extensions.xml` file in your root project. The `extensions.xml` file
is the same file where you have already declared the Develocity Maven extension.

See [here](.mvn/extensions.xml) for an example.

### Version compatibility

This table details the version compatibility of the Common Custom User Data Maven extension with the Develocity Maven extension.

| Common Custom User Data Maven extension versions | Develocity Maven extension versions        |
| ------------------------------------------------ | ------------------------------------------ |
| `1.8+`                                           | `1.11+`                                    |
| `1.7` - `1.7.3`                                  | `1.10.1+`                                  |
| `1.3` - `1.6`                                    | `1.6.5+`                                   |
| `1.0` - `1.2`                                    | `1.0+`                                     |

The above chart captures the minimum compatible versions. For the best experience with the Develocity Maven extension 1.21 or higher, we recommend using the Common Custom User Data Maven extension 2.0 or higher.

## Captured data

The additional tags, links and custom values captured by this extension include:
- A tag representing the operating system
- A tag representing how the build was invoked, be that from your IDE (IDEA, Eclipse) or from the command-line
- A tag representing builds run on CI, together with a set of tags, links and custom values specific to the CI server running the build
- For Git repositories, information on the commit id, branch name, status, and whether the checkout is dirty or not

See [CustomBuildScanEnhancements.java](./src/main/java/com/gradle/CustomBuildScanEnhancements.java) for details on what data is
captured and under which conditions.

## Capturing additional tags, links and values in your build scans

You can apply additional configuration beyond what is contributed by the Common Custom User Data Maven extension by default. The additional configuration happens in a specific
Groovy script. This is a good intermediate step before creating your own extension.

The Common Custom User Data Maven extension checks for a `.mvn/develocity-custom-user-data.groovy` or `.mvn/gradle-enterprise-custom-user-data.groovy` Groovy script in your root project. If the file exists, it evaluates
the script with the following bindings:

- `develocity/gradleEnterprise` (type: [DevelocityAdapter](https://github.com/gradle/develocity-agent-adapters/blob/main/develocity-maven-extension-adapters/src/compatibilityApi/java/com/gradle/develocity/agent/maven/adapters/DevelocityAdapter.java)): _configure Develocity_
- `buildScan` (type: [BuildScanApiAdapter](https://github.com/gradle/develocity-agent-adapters/blob/main/develocity-maven-extension-adapters/src/compatibilityApi/java/com/gradle/develocity/agent/maven/adapters/BuildScanApiAdapter.java)): _configure build scan publishing and enhance build scans_
- `buildCache` (type: [BuildCacheApiAdapter](https://github.com/gradle/develocity-agent-adapters/blob/main/develocity-maven-extension-adapters/src/compatibilityApi/java/com/gradle/develocity/agent/maven/adapters/BuildCacheApiAdapter.java)): _configure build cache_
- `log` (type: [`Logger`](http://www.slf4j.org/apidocs/org/slf4j/Logger.html)): _write to the build log_
- `project` (type: [`MavenProject`](https://maven.apache.org/ref/current/maven-core/apidocs/org/apache/maven/project/MavenProject.html)): _the top-level Maven project_
- `session` (type: [`MavenSession`](https://maven.apache.org/ref/current/maven-core/apidocs/org/apache/maven/execution/MavenSession.html)): _the Maven session_

See [here](.mvn/develocity-custom-user-data.groovy) for an example.

## Developing a customized version of the extension

For more flexibility, we recommend creating a copy of this repository so that you may develop a customized version of the extension and publish it internally for your projects to consume.

This approach has a number of benefits:
- Tailor the build scan enhancements to exactly the set of tags, links and custom values you require
- Standardize the configuration for connecting to Develocity and the remote build cache in your organization, removing the need for each project to specify this configuration

If your customized extension provides all required Develocity configuration, then a consumer project will get all the benefits of Develocity simply by applying the extension. The
project sources provide a good template to get started with your own extension.

Refer to the [Javadoc](https://docs.gradle.com/enterprise/maven-extension/api/) for more details on the key types available for use.

## Changelog

Refer to the [release history](https://github.com/gradle/common-custom-user-data-maven-extension/releases) to see detailed changes on the versions.

## Breaking API changes

When updating to the Common Custom User Data Maven extension 2.0, please take care of the following.

1. Rename the `.mvn/gradle-enterprise-custom-user-data.groovy` to `.mvn/develocity-custom-user-data.groovy`.
2. Use `BuildScanApiAdapter` instead of `BuildScanApi`. See the example below:

`gradle-enterprise-custom-user-data.groovy`
```
import com.gradle.maven.extension.api.scan.BuildScanApi

buildScan.executeOnce('top-level-project') { BuildScanApi buildScanApi -> }
```
`develocity-custom-user-data.groovy`
```
import com.gradle.develocity.agent.maven.adapters.BuildScanApiAdapter

buildScan.executeOnce('top-level-project') { BuildScanApiAdapter buildScanApi -> }
```

## Learn more

Visit our website to learn more about [Develocity][develocity].

## License

The Develocity Common Custom User Data Maven extension is open-source software released under the [Apache 2.0 License][apache-license].

[develocity-build-config-samples]: https://github.com/gradle/develocity-build-config-samples
[develocity-build-validation-scripts]: https://github.com/gradle/gradle-enterprise-build-validation-scripts
[develocity-oss-projects]: https://github.com/gradle/develocity-oss-projects
[ccud-gradle-plugin]: https://github.com/gradle/common-custom-user-data-gradle-plugin
[ccud-maven-extension]: https://github.com/gradle/common-custom-user-data-maven-extension
[ccud-sbt-plugin]: https://github.com/gradle/common-custom-user-data-sbt-plugin
[android-cache-fix-plugin]: https://github.com/gradle/android-cache-fix-gradle-plugin
[develocity]: https://gradle.com/develocity
[apache-license]: https://www.apache.org/licenses/LICENSE-2.0.html
