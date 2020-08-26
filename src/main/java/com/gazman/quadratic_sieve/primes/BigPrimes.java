package com.gazman.quadratic_sieve.primes;

import com.gazman.quadratic_sieve.data.BSmooth;
import com.gazman.quadratic_sieve.data.DataQueue;
import com.gazman.quadratic_sieve.debug.Analytics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BigPrimes {

    public static final BigPrimes instance = new BigPrimes();

    private final Map<Long, BSmooth> bigPrimesMap = new ConcurrentHashMap<>();
    private final AtomicInteger totalBigPrimes = new AtomicInteger();

    public int getTotalBigPrimes() {
        return totalBigPrimes.get();
    }

    public void addBigPrime(BSmooth bSmooth, long bigPrime) {
        totalBigPrimes.getAndIncrement();
        BSmooth currentValue = bigPrimesMap.putIfAbsent(bigPrime, bSmooth);
        if (currentValue == null) {
            return;
        }

        xor(bSmooth, currentValue);
        try {
            bSmooth.bigPrime = true;
            Analytics.VECTOR_EXTRACTOR_QUEUE.start();
            DataQueue.bSmooths.put(bSmooth);
            Analytics.VECTOR_EXTRACTOR_QUEUE.end();
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    private void xor(BSmooth bSmooth, BSmooth currentValue) {
        bSmooth.a = currentValue.getA().multiply(bSmooth.getA());
        bSmooth.b = currentValue.getB().multiply(bSmooth.getB());
        bSmooth.vector.xor(currentValue.vector);
        bSmooth.originalVector.xor(currentValue.vector);
    }
}
