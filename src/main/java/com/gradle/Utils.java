package com.gradle;

import org.apache.maven.execution.MavenSession;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Utils {

    private static final Pattern GIT_REPO_URI_PATTERN = Pattern.compile("^(?:(?:https://|git://)(?:.+:.+@)?|(?:ssh)?.*?@)(.*?(?:github|gitlab).*?)(?:/|:[0-9]*?/|:)(.*?)(?:\\.git)?$");

    static Optional<String> envVariable(String name) {
        return Optional.ofNullable(System.getenv(name));
    }

    static Optional<String> projectProperty(MavenSession mavenSession, String name) {
        String value = mavenSession.getUserProperties().getProperty(name);
        return Optional.ofNullable(value);
    }

    static Optional<String> sysProperty(String name) {
        return Optional.ofNullable(System.getProperty(name));
    }

    static Optional<Boolean> booleanSysProperty(String name) {
        return sysProperty(name).map(Boolean::parseBoolean);
    }

    static Optional<Duration> durationSysProperty(String name) {
        return sysProperty(name).map(Duration::parse);
    }

    static boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }

    static String appendIfMissing(String str, String suffix) {
        return str.endsWith(suffix) ? str : str + suffix;
    }

    static String urlEncode(String str) {
        try {
            return URLEncoder.encode(str, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    static String redactUserInfo(String url) {
        try {
            String userInfo = new URI(url).getUserInfo();
            return userInfo == null
                ? url
                : url.replace(userInfo + '@', "******@");
        } catch (URISyntaxException e) {
            return url;
        }
    }

    static Properties readPropertiesFile(String name) {
        try (InputStream input = new FileInputStream(name)) {
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static boolean execAndCheckSuccess(String... args) {
        Runtime runtime = Runtime.getRuntime();
        Process process = null;
        try {
            process = runtime.exec(args);
            boolean finished = process.waitFor(10, TimeUnit.SECONDS);
            return finished && process.exitValue() == 0;
        } catch (IOException | InterruptedException ignored) {
            return false;
        } finally {
            if (process != null) {
                process.destroyForcibly();
            }
        }
    }

    static String execAndGetStdOut(String... args) {
        Runtime runtime = Runtime.getRuntime();
        Process process;
        try {
            process = runtime.exec(args);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (Reader standard = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.defaultCharset()))) {
            try (Reader error = new BufferedReader(new InputStreamReader(process.getErrorStream(), Charset.defaultCharset()))) {
                String standardText = readFully(standard);
                String ignore = readFully(error);

                boolean finished = process.waitFor(10, TimeUnit.SECONDS);
                return finished && process.exitValue() == 0 ? trimAtEnd(standardText) : null;
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            process.destroyForcibly();
        }
    }

    /**
     * Construct a repo {@link URI} from a git URL in the format of
     * <code>git://github.com/acme-inc/my-project.git</code>. If the URL cannot be parsed, {@link Optional#empty()} is
     * returned.
     * <p>
     * The scheme can be any of <code>git://</code>, <code>https://</code>, or <code>ssh</code>.
     */
    static Optional<URI> toWebRepoUri(String gitRepoUri) {
        Matcher matcher = GIT_REPO_URI_PATTERN.matcher(gitRepoUri);
        if (matcher.matches()) {
            String scheme = "https";
            String host = matcher.group(1);
            String path = matcher.group(2).startsWith("/") ? matcher.group(2) : "/" + matcher.group(2);
            return toUri(scheme, host, path);
        } else {
            return Optional.empty();
        }
    }

    static Optional<URI> toUri(String scheme, String host, String path) {
        try {
            return Optional.of(new URI(scheme, host, path, null));
        } catch (URISyntaxException e) {
            return Optional.empty();
        }
    }

    private static String readFully(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        char[] buf = new char[1024];
        int nRead;
        while ((nRead = reader.read(buf)) != -1) {
            sb.append(buf, 0, nRead);
        }
        return sb.toString();
    }

    private static String trimAtEnd(String str) {
        return ('x' + str).trim().substring(1);
    }

    private Utils() {
    }

}
