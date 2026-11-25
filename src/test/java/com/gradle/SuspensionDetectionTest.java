package com.gradle;

import com.gradle.develocity.agent.maven.adapters.BuildResultAdapter;
import com.gradle.develocity.agent.maven.adapters.BuildScanApiAdapter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.LongSupplier;

import static com.gradle.SuspensionDetection.THREAD_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SuspensionDetectionTest {

    // Short enough that scheduled samples arrive quickly
    private static final long INTERVAL_MILLIS = 10;
    private static final long THRESHOLD = 10 * INTERVAL_MILLIS;
    private static final long AWAIT_SECONDS = 5;

    @Test
    public void addsNothingWhenTheBuildWasNotSuspended() throws Exception {
        RecordingBuildScan buildScan = new RecordingBuildScan();
        StepClock clock = new StepClock();
        SuspensionDetection.apply(buildScan.adapter, clock, INTERVAL_MILLIS, THRESHOLD);

        // The clock moves further than the threshold in total, so only the periodic samples keep the suspended time at zero
        for (int i = 0; i < 15; i++) {
            clock.advanceAndAwaitSample(INTERVAL_MILLIS);
        }
        buildScan.finish();

        assertEquals(Collections.emptyList(), buildScan.tags);
        assertEquals(Collections.emptyList(), buildScan.values);
    }

    @Test
    public void tagsTheBuildAndRecordsSuspendedTime() throws Exception {
        RecordingBuildScan buildScan = new RecordingBuildScan();
        StepClock clock = new StepClock();
        SuspensionDetection.apply(buildScan.adapter, clock, INTERVAL_MILLIS, THRESHOLD);

        for (int i = 0; i < 3; i++) {
            clock.advanceAndAwaitSample(INTERVAL_MILLIS);
        }
        clock.advanceAndAwaitSample(INTERVAL_MILLIS + 60_000);
        for (int i = 0; i < 3; i++) {
            clock.advanceAndAwaitSample(INTERVAL_MILLIS);
        }
        buildScan.finish();

        assertEquals(Collections.singletonList("Suspended"), buildScan.tags);
        assertEquals(Collections.singletonList("Suspended time (ms)=60000"), buildScan.values);
    }

    @Test
    public void countsASuspensionThatEndedAfterTheLastScheduledSample() {
        long interval = TimeUnit.HOURS.toMillis(1);
        RecordingBuildScan buildScan = new RecordingBuildScan();
        StepClock clock = new StepClock();
        SuspensionDetection.apply(buildScan.adapter, clock, interval, THRESHOLD);

        clock.time.addAndGet(interval + 30_000);
        buildScan.finish();

        assertEquals(Collections.singletonList("Suspended"), buildScan.tags);
        assertEquals(Collections.singletonList("Suspended time (ms)=30000"), buildScan.values);
    }

    @Test
    public void registersOneBuildFinishedCallback() {
        RecordingBuildScan buildScan = new RecordingBuildScan();
        SuspensionDetection.apply(buildScan.adapter, new StepClock(), INTERVAL_MILLIS, THRESHOLD);

        assertEquals(1, buildScan.finished.size());

        buildScan.finish();
    }

    @Test
    public void samplesOnADaemonThreadThatEndsWithTheBuild() throws Exception {
        Set<Thread> before = samplerThreads();
        RecordingBuildScan buildScan = new RecordingBuildScan();
        SuspensionDetection.apply(buildScan.adapter, new StepClock(), INTERVAL_MILLIS, THRESHOLD);
        Thread sampler = onlyNewThread(before);

        assertTrue(sampler.isDaemon());

        buildScan.finish();
        sampler.join(TimeUnit.SECONDS.toMillis(AWAIT_SECONDS));

        assertFalse(sampler.isAlive());
    }

    @Test
    public void tagsAFailedBuild() {
        long interval = TimeUnit.HOURS.toMillis(1);
        RecordingBuildScan buildScan = new RecordingBuildScan();
        StepClock clock = new StepClock();
        SuspensionDetection.apply(buildScan.adapter, clock, interval, THRESHOLD);

        clock.time.addAndGet(interval + 30_000);
        buildScan.finish((BuildResultAdapter) () -> Collections.singletonList(new RuntimeException("boom")));

        assertEquals(Collections.singletonList("Suspended"), buildScan.tags);
        assertEquals(Collections.singletonList("Suspended time (ms)=30000"), buildScan.values);
    }

    @Test
    public void nextBuildStopsTheSamplerOfABuildThatNeverFinished() throws Exception {
        Set<Thread> before = samplerThreads();
        SuspensionDetection.apply(new RecordingBuildScan().adapter, new StepClock(), INTERVAL_MILLIS, THRESHOLD);
        Thread leftover = onlyNewThread(before);

        RecordingBuildScan next = new RecordingBuildScan();
        SuspensionDetection.apply(next.adapter, new StepClock(), INTERVAL_MILLIS, THRESHOLD);
        leftover.join(TimeUnit.SECONDS.toMillis(AWAIT_SECONDS));

        assertFalse(leftover.isAlive());

        next.finish();
    }

    @Test
    public void lateFinishOfAnEarlierBuildLetsTheNextBuildStopTheCurrentSampler() throws Exception {
        RecordingBuildScan earlier = new RecordingBuildScan();
        SuspensionDetection.apply(earlier.adapter, new StepClock(), INTERVAL_MILLIS, THRESHOLD);
        Set<Thread> before = samplerThreads();
        SuspensionDetection.apply(new RecordingBuildScan().adapter, new StepClock(), INTERVAL_MILLIS, THRESHOLD);
        Thread unfinished = onlyNewThread(before);
        earlier.finish();

        RecordingBuildScan next = new RecordingBuildScan();
        SuspensionDetection.apply(next.adapter, new StepClock(), INTERVAL_MILLIS, THRESHOLD);
        unfinished.join(TimeUnit.SECONDS.toMillis(AWAIT_SECONDS));

        assertFalse(unfinished.isAlive());

        next.finish();
    }

    private static Set<Thread> samplerThreads() {
        Set<Thread> threads = new HashSet<>();
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (THREAD_NAME.equals(thread.getName()) && thread.isAlive()) {
                threads.add(thread);
            }
        }
        return threads;
    }

    private static Thread onlyNewThread(Set<Thread> before) {
        Set<Thread> added = samplerThreads();
        added.removeAll(before);
        assertEquals(1, added.size());
        return added.iterator().next();
    }

    // Reads return the value the test last set, so a sample is as late as the test makes it
    private static final class StepClock implements LongSupplier {

        private final AtomicLong time = new AtomicLong(1_000_000);
        private final Semaphore reads = new Semaphore(0);

        @Override
        public long getAsLong() {
            long value = time.get();
            reads.release();
            return value;
        }

        // The first read after the drain may have started before the test set the new time. The second read cannot have.
        void advanceAndAwaitSample(long millis) throws InterruptedException {
            time.addAndGet(millis);
            reads.drainPermits();
            assertTrue(reads.tryAcquire(2, AWAIT_SECONDS, TimeUnit.SECONDS));
        }

    }

    private static final class RecordingBuildScan {

        private final List<String> tags = new ArrayList<>();
        private final List<String> values = new ArrayList<>();
        private final List<Consumer<Object>> finished = new ArrayList<>();
        private final BuildScanApiAdapter adapter = (BuildScanApiAdapter) Proxy.newProxyInstance(
            BuildScanApiAdapter.class.getClassLoader(),
            new Class<?>[]{BuildScanApiAdapter.class},
            (proxy, method, args) -> {
                switch (method.getName()) {
                    case "tag":
                        tags.add((String) args[0]);
                        break;
                    case "value":
                        values.add(args[0] + "=" + args[1]);
                        break;
                    case "buildFinished":
                        @SuppressWarnings("unchecked")
                        Consumer<Object> callback = (Consumer<Object>) args[0];
                        finished.add(callback);
                        break;
                    default:
                        throw new UnsupportedOperationException(method.getName());
                }
                return null;
            });

        void finish() {
            finish(null);
        }

        void finish(BuildResultAdapter result) {
            for (Consumer<Object> callback : finished) {
                callback.accept(result);
            }
        }

    }

}
