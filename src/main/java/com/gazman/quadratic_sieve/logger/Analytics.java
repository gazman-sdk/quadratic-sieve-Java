package com.gazman.quadratic_sieve.logger;

import java.util.concurrent.atomic.AtomicLong;

import static com.gazman.quadratic_sieve.QuadraticSieve.DEBUG;
import static com.gazman.quadratic_sieve.logger.Logger.formatLong;

public enum Analytics {
    POLY_MINER,
    SIEVER_TOTAL,
    SIEVE_CORE,
    SIEVE_COLLECT,
    SIEVE_RE_SIEVE,
    SIEVE_QUEUE_OUT,
    VE_TOTAL,
    VE_PRIME,
    VE_POWERS,
    VE_DIVIDE,
    VE_TEST_END,
    MATRIX,
    ;

    private final String formattedName = super.toString().toLowerCase().replaceAll("_", "-");
    private final ThreadLocal<Long> startTimeNano = ThreadLocal.withInitial(System::nanoTime);
    private final AtomicLong totalTimeNano = new AtomicLong();

    public void start() {
        if (!DEBUG) {
            return;
        }
        startTimeNano.set(System.nanoTime());
    }

    public void end() {
        if (!DEBUG) {
            return;
        }
        totalTimeNano.addAndGet(System.nanoTime() - startTimeNano.get());
    }

    public long getTotalTimeNano() {
        return totalTimeNano.get();
    }


    @Override
    public String toString() {
        String totalTimeMillis = formatLong(getTotalTimeNano() / 1_000_000);
        return formattedName + " " + totalTimeMillis;
    }
}
