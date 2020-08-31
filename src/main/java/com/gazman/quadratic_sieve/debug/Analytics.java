package com.gazman.quadratic_sieve.debug;

import java.util.concurrent.atomic.AtomicLong;

import static com.gazman.quadratic_sieve.debug.Logger.formatLong;

public enum Analytics {
    POLY_MINER_TOTAL(-1),
    MATRIX(-1),
    SIEVER_BUILD_WHEELS(0),
    SIEVER_STATIC_WHEELS(0),
    SIEVE_CORE(0),
    SIEVE_COLLECT(0),
    SIEVE_RE_SIEVE_1(0),
    SIEVE_RE_SIEVE_2(0),
    SIEVE_VECTOR_EXTRACTOR(0),
    SIEVE_QUEUE_POLY(0),
    SIEVE_QUEUE_B_SMOOTH(0);

    private static final AtomicLong[] groups = {new AtomicLong(), new AtomicLong()};
    private final int group;
    @SuppressWarnings("DynamicRegexReplaceableByCompiledPattern")
    private final String formattedName = super.toString().toLowerCase().replaceAll("_", "-");
    private static final ThreadLocal<Long> startTimeNano = ThreadLocal.withInitial(System::nanoTime);
    private final AtomicLong totalTimeNano = new AtomicLong();

    Analytics(int group) {
        this.group = group;
    }

    public static void start() {
        startTimeNano.set(System.nanoTime());
    }

    public void end() {
        long currentTime = System.nanoTime();
        totalTimeNano.addAndGet(currentTime - startTimeNano.get());
        if (group != -1) {
            groups[group].addAndGet(currentTime - startTimeNano.get());
        }
    }

    public long getTotalTimeNano() {
        return totalTimeNano.get();
    }


    @Override
    public String toString() {
        String totalTimeMillis = formatLong(getTotalTimeNano() / 1_000_000);
        if (group != -1) {
            return formattedName + " " + totalTimeMillis + "(" + Logger.formatDouble(
                    (double) getTotalTimeNano() / groups[group].get() * 100, 2) + "%)";
        }
        return formattedName + " " + totalTimeMillis;
    }
}
