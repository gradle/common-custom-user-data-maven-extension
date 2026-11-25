package com.gradle;

import com.gradle.develocity.agent.maven.adapters.BuildScanApiAdapter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

import static com.gradle.CommonCustomUserDataDevelocityLogger.LOGGER;

final class SuspensionDetection {

    static final long SAMPLE_INTERVAL_MILLIS = 1_000;
    // Below this, a late sample can come from a busy machine or a long GC pause rather than from a suspended one
    static final long THRESHOLD_MILLIS = 10_000;

    static final String THREAD_NAME = "Common Custom User Data suspension detector";

    private static Sampler sampler;

    private SuspensionDetection() {
    }

    static void apply(BuildScanApiAdapter buildScan) {
        apply(buildScan, System::currentTimeMillis, SAMPLE_INTERVAL_MILLIS, THRESHOLD_MILLIS);
    }

    static void apply(BuildScanApiAdapter buildScan, LongSupplier wallClockMillis, long intervalMillis, long thresholdMillis) {
        Sampler started = startSampling(wallClockMillis, intervalMillis, thresholdMillis);
        buildScan.buildFinished(result -> {
            long suspendedMillis = stopSampling(started);
            if (suspendedMillis > 0) {
                LOGGER.debug("The build process was suspended for {} ms", suspendedMillis);
                buildScan.tag("Suspended");
                buildScan.value("Suspended time (ms)", Long.toString(suspendedMillis));
            }
        });
    }

    // The Develocity extension skips buildFinished when Build Scan capture is disabled,
    // so the next build in a reused JVM such as an mvnd daemon stops the sampler instead
    private static synchronized Sampler startSampling(LongSupplier wallClockMillis, long intervalMillis, long thresholdMillis) {
        if (sampler != null) {
            sampler.stop();
        }
        sampler = new Sampler(wallClockMillis, intervalMillis, thresholdMillis);
        return sampler;
    }

    private static synchronized long stopSampling(Sampler finished) {
        if (sampler == finished) {
            sampler = null;
        }
        return finished.stop();
    }

    private static final class Sampler {

        private final SuspensionDetector detector;
        private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, THREAD_NAME);
            thread.setDaemon(true);
            return thread;
        });

        private Sampler(LongSupplier wallClockMillis, long intervalMillis, long thresholdMillis) {
            detector = new SuspensionDetector(wallClockMillis, intervalMillis, thresholdMillis);
            executor.scheduleWithFixedDelay(detector::sample, intervalMillis, intervalMillis, TimeUnit.MILLISECONDS);
        }

        private long stop() {
            executor.shutdownNow();
            // The final sample counts a suspension that ended after the previous sample
            detector.sample();
            return detector.getSuspendedMillis();
        }

    }

}
