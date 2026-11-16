package com.gradle;

import com.gradle.develocity.agent.maven.adapters.BuildScanApiAdapter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

final class SuspensionDetection {

    static final long SAMPLE_INTERVAL_MILLIS = 1_000;
    // Below this, a late sample can come from a busy machine or a long GC pause rather than from a suspended one
    static final long THRESHOLD_MILLIS = 10_000;

    static final String THREAD_NAME = "Common Custom User Data suspension detector";

    private SuspensionDetection() {
    }

    static void apply(BuildScanApiAdapter buildScan) {
        apply(buildScan, System::currentTimeMillis, SAMPLE_INTERVAL_MILLIS, THRESHOLD_MILLIS);
    }

    // Each build gets its own sampler, so sessions that share a JVM under mvnd do not share suspended time
    static void apply(BuildScanApiAdapter buildScan, LongSupplier wallClockMillis, long intervalMillis, long thresholdMillis) {
        Sampler sampler = new Sampler(wallClockMillis, intervalMillis, thresholdMillis);
        buildScan.buildFinished(result -> {
            long suspendedMillis = sampler.stop();
            if (suspendedMillis > 0) {
                buildScan.tag("Suspended");
                buildScan.value("Suspended time (ms)", Long.toString(suspendedMillis));
            }
        });
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
