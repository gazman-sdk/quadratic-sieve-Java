package com.gazman.quadratic_sieve.debug;

import java.util.concurrent.atomic.AtomicLong;

import static com.gazman.quadratic_sieve.debug.Logger.formatLong;

public enum Analytics {
    POLY_MINER_TOTAL(-1),
    SIEVER_WHEELS(0),
    SIEVE_CORE(0),
    SIEVE_COLLECT(0),
    SIEVE_RE_SIEVE(0),
    SIEVE_QUEUE_OUT(0),
    VECTOR_EXTRACTOR_TOTAL(0),
    MATRIX(-1),
    VECTOR_EXTRACTOR_QUEUE(0);

    private final int group;

    Analytics(int group) {
        this.group = group;
    }

    private final String formattedName = super.toString().toLowerCase().replaceAll("_", "-");
    private final ThreadLocal<Long> startTimeNano = ThreadLocal.withInitial(System::nanoTime);
    private final AtomicLong totalTimeNano = new AtomicLong();
    private static final AtomicLong[] groups = {new AtomicLong(), new AtomicLong()};

    public void start() {
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
