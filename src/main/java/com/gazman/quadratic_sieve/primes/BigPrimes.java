package com.gazman.quadratic_sieve.primes;

import com.gazman.quadratic_sieve.data.BSmooth;
import com.gazman.quadratic_sieve.data.DataQueue;

import java.util.HashMap;

public class BigPrimes {

    public static final BigPrimes instance = new BigPrimes();

    private final HashMap<Long, BSmooth> bigPrimesMap = new HashMap<>();
    private int totalBigPrimes;

    public synchronized int getTotalBigPrimes() {
        return totalBigPrimes;
    }

    public synchronized void addBigPrime(BSmooth bSmooth, long bigPrime) {
        totalBigPrimes++;
        BSmooth currentValue = bigPrimesMap.putIfAbsent(bigPrime, bSmooth);
        if (currentValue == null) {
            return;
        }

        xor(bSmooth, currentValue);
        try {
            bSmooth.bigPrime = true;
            DataQueue.bSmooths.put(bSmooth);
        } catch (InterruptedException ignore) {

        }
    }

    private void xor(BSmooth bSmooth, BSmooth currentValue) {
        bSmooth.a = currentValue.getA().multiply(bSmooth.getA());
        bSmooth.b = currentValue.getB().multiply(bSmooth.getB());
        bSmooth.vector.xor(currentValue.vector);
        bSmooth.originalVector.xor(currentValue.vector);
    }
}
