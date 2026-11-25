package com.gradle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

// SIGSTOP freezes the Maven JVM the way a sleeping laptop does: every thread stops, and the wall clock keeps running
@EnabledOnOs({OS.LINUX, OS.MAC})
public class SuspensionDetectionIT {

    private static final Pattern SUSPENDED_LINE = Pattern.compile("The build process was suspended for (\\d+) ms");
    // Longer than the detection threshold plus one sample interval, so the delay always counts
    private static final long SUSPEND_MILLIS = 13_000;

    @TempDir
    Path projectDir;

    private Path signals;

    @BeforeEach
    public void createProject() throws IOException {
        signals = projectDir.resolve("signals");
        Files.createDirectories(signals);
        write(".mvn/extensions.xml", String.join("\n",
            "<extensions>",
            "    <extension>",
            "        <groupId>com.gradle</groupId>",
            "        <artifactId>develocity-maven-extension</artifactId>",
            "        <version>2.6.1</version>",
            "    </extension>",
            "</extensions>",
            ""));
        write(".mvn/develocity.xml", String.join("\n",
            "<develocity>",
            "    <buildScan>",
            "        <publishing>",
            "            <onlyIf>false</onlyIf>",
            "        </publishing>",
            "    </buildScan>",
            "</develocity>",
            ""));
        write("pom.xml", String.join("\n",
            "<project>",
            "    <modelVersion>4.0.0</modelVersion>",
            "    <groupId>com.example</groupId>",
            "    <artifactId>suspended</artifactId>",
            "    <version>1.0</version>",
            "    <packaging>pom</packaging>",
            "    <build>",
            "        <plugins>",
            "            <plugin>",
            "                <artifactId>maven-antrun-plugin</artifactId>",
            "                <version>3.1.0</version>",
            "                <executions>",
            "                    <execution>",
            "                        <id>await-release</id>",
            "                        <phase>validate</phase>",
            "                        <goals><goal>run</goal></goals>",
            "                        <configuration>",
            "                            <target>",
            "                                <touch file=\"signals/started\"/>",
            "                                <waitfor maxwait=\"2\" maxwaitunit=\"minute\">",
            "                                    <available file=\"signals/release\"/>",
            "                                </waitfor>",
            "                            </target>",
            "                        </configuration>",
            "                    </execution>",
            "                </executions>",
            "            </plugin>",
            "        </plugins>",
            "    </build>",
            "</project>",
            ""));
    }

    @Test
    public void reportsTheSuspendedTime() throws Exception {
        String output = runSuspended();

        Matcher matcher = SUSPENDED_LINE.matcher(output);
        assertTrue(matcher.find(), output);
        long suspendedMillis = Long.parseLong(matcher.group(1));
        long minimumMillis = SUSPEND_MILLIS - SuspensionDetection.SAMPLE_INTERVAL_MILLIS;
        assertTrue(suspendedMillis >= minimumMillis, "suspended for " + suspendedMillis + " ms, expected at least " + minimumMillis);
        assertFalse(matcher.find(), "reported more than once: " + output);
    }

    @Test
    public void reportsNothingWhenTheBuildWasNotSuspended() throws Exception {
        Files.createFile(signals.resolve("release"));

        Process maven = startMaven();
        String output = awaitMaven(maven);

        assertFalse(SUSPENDED_LINE.matcher(output).find(), output);
    }

    private String runSuspended() throws Exception {
        Process maven = startMaven();
        awaitStarted(maven);
        // bin/mvn execs the JVM, so this pid is the JVM running the build
        String pid = Long.toString(maven.pid());

        signal("-STOP", pid);
        try {
            Thread.sleep(SUSPEND_MILLIS);
        } finally {
            signal("-CONT", pid);
        }

        Files.createFile(signals.resolve("release"));
        return awaitMaven(maven);
    }

    private Process startMaven() throws IOException {
        Path mvn = Paths.get(System.getProperty("it.maven.home"), "bin", "mvn");
        return new ProcessBuilder(mvn.toString(), "-B", "-X",
            "-Dmaven.ext.class.path=" + System.getProperty("it.extension.jar"),
            "validate")
            .directory(projectDir.toFile())
            .redirectErrorStream(true)
            .redirectOutput(projectDir.resolve("build.log").toFile())
            .start();
    }

    private void awaitStarted(Process maven) throws Exception {
        long deadline = System.nanoTime() + TimeUnit.MINUTES.toNanos(2);
        while (!Files.exists(signals.resolve("started"))) {
            if (!maven.isAlive()) {
                throw new AssertionError("The build finished before it reached the pause:\n" + readLog());
            }
            if (System.nanoTime() > deadline) {
                throw new TimeoutException("The build never reached the pause");
            }
            Thread.sleep(50);
        }
    }

    private String awaitMaven(Process maven) throws Exception {
        if (!maven.waitFor(2, TimeUnit.MINUTES)) {
            maven.destroyForcibly();
            throw new TimeoutException("The build did not finish:\n" + readLog());
        }
        String output = readLog();
        assertEquals(0, maven.exitValue(), output);
        return output;
    }

    private String readLog() throws IOException {
        return new String(Files.readAllBytes(projectDir.resolve("build.log")), StandardCharsets.UTF_8);
    }

    private static void signal(String signal, String pid) throws IOException, InterruptedException {
        Process kill = new ProcessBuilder("kill", signal, pid).inheritIO().start();
        assertEquals(0, kill.waitFor(), "kill " + signal + " " + pid);
    }

    private void write(String path, String content) throws IOException {
        Path file = projectDir.resolve(path);
        Files.createDirectories(file.getParent());
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
    }

}
