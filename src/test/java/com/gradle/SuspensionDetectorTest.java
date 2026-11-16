package com.gradle;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import static com.gradle.SuspensionDetection.SAMPLE_INTERVAL_MILLIS;
import static com.gradle.SuspensionDetection.THRESHOLD_MILLIS;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SuspensionDetectorTest {

    private final AtomicLong now = new AtomicLong(1_000_000);
    private final SuspensionDetector detector = new SuspensionDetector(now::get, SAMPLE_INTERVAL_MILLIS, THRESHOLD_MILLIS);

    @Test
    public void reportsNothingWhenSamplesArriveOnTime() {
        for (int i = 0; i < 60; i++) {
            advanceAndSample(SAMPLE_INTERVAL_MILLIS);
        }

        assertEquals(0, detector.getSuspendedMillis());
    }

    @Test
    public void ignoresDelaysBelowThreshold() {
        advanceAndSample(SAMPLE_INTERVAL_MILLIS + THRESHOLD_MILLIS - 1);

        assertEquals(0, detector.getSuspendedMillis());
    }

    @Test
    public void doesNotAddUpDelaysThatAreEachBelowThreshold() {
        for (int i = 0; i < 9; i++) {
            advanceAndSample(SAMPLE_INTERVAL_MILLIS + THRESHOLD_MILLIS - 1);
        }

        assertEquals(0, detector.getSuspendedMillis());
    }

    @Test
    public void countsDelayEqualToThreshold() {
        advanceAndSample(SAMPLE_INTERVAL_MILLIS + THRESHOLD_MILLIS);

        assertEquals(THRESHOLD_MILLIS, detector.getSuspendedMillis());
    }

    @Test
    public void countsTimeBeyondTheSampleIntervalAsSuspended() {
        advanceAndSample(SAMPLE_INTERVAL_MILLIS);
        advanceAndSample(SAMPLE_INTERVAL_MILLIS + 8 * 3_600_000);
        advanceAndSample(SAMPLE_INTERVAL_MILLIS);

        assertEquals(8 * 3_600_000, detector.getSuspendedMillis());
    }

    @Test
    public void addsUpSeveralSuspensions() {
        advanceAndSample(SAMPLE_INTERVAL_MILLIS + 60_000);
        advanceAndSample(SAMPLE_INTERVAL_MILLIS);
        advanceAndSample(SAMPLE_INTERVAL_MILLIS + 30_000);

        assertEquals(90_000, detector.getSuspendedMillis());
    }

    @Test
    public void measuresTheFirstSampleFromConstruction() {
        advanceAndSample(SAMPLE_INTERVAL_MILLIS + 45_000);

        assertEquals(45_000, detector.getSuspendedMillis());
    }

    @Test
    public void ignoresSampleThatArrivesBeforeTheInterval() {
        advanceAndSample(SAMPLE_INTERVAL_MILLIS / 2);

        assertEquals(0, detector.getSuspendedMillis());
    }

    @Test
    public void measuresFollowingSampleFromAnEarlySample() {
        advanceAndSample(SAMPLE_INTERVAL_MILLIS / 2);
        advanceAndSample(SAMPLE_INTERVAL_MILLIS + 20_000);

        assertEquals(20_000, detector.getSuspendedMillis());
    }

    @Test
    public void ignoresWallClockSetBackwards() {
        advanceAndSample(-3_600_000);
        advanceAndSample(SAMPLE_INTERVAL_MILLIS);

        assertEquals(0, detector.getSuspendedMillis());
    }

    @Test
    public void keepsSuspendedTimeWhenWallClockIsSetBackwardsAfterwards() {
        advanceAndSample(SAMPLE_INTERVAL_MILLIS + 60_000);
        advanceAndSample(-3_600_000);
        advanceAndSample(SAMPLE_INTERVAL_MILLIS);

        assertEquals(60_000, detector.getSuspendedMillis());
    }

    @Test
    public void countsWallClockSetForwardAsSuspended() {
        advanceAndSample(SAMPLE_INTERVAL_MILLIS);
        advanceAndSample(3_600_000);

        assertEquals(3_600_000 - SAMPLE_INTERVAL_MILLIS, detector.getSuspendedMillis());
    }

    @Test
    public void countsOneSuspensionOnceWhenSampledConcurrently() throws InterruptedException {
        now.addAndGet(SAMPLE_INTERVAL_MILLIS + 60_000);
        CountDownLatch start = new CountDownLatch(1);
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            Thread thread = new Thread(() -> {
                awaitQuietly(start);
                detector.sample();
            });
            thread.start();
            threads.add(thread);
        }

        start.countDown();
        for (Thread thread : threads) {
            thread.join();
        }

        assertEquals(60_000, detector.getSuspendedMillis());
    }

    private static void awaitQuietly(CountDownLatch latch) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void advanceAndSample(long millis) {
        now.addAndGet(millis);
        detector.sample();
    }

}
