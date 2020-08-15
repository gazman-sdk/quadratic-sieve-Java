package com.gazman.quadratic_sieve.primes;

import com.gazman.quadratic_sieve.data.BSmooth;
import com.gazman.quadratic_sieve.data.DataQueue;

import java.math.BigInteger;
import java.util.HashMap;

public class BigPrimes {

    public static final BigPrimes instance = new BigPrimes();

    private final HashMap<BigInteger, BSmooth> bigPrimesMap = new HashMap<>();
    private int totalBigPrimes;
    private int usedBigPrimesCount;

    public synchronized int getTotalBigPrimes() {
        return totalBigPrimes;
    }

    public synchronized int getUsedBigPrimesCount() {
        return usedBigPrimesCount;
    }

    public synchronized boolean addBigPrime(BSmooth bSmooth, BigInteger bigPrime) {
        totalBigPrimes++;
        BSmooth currentValue = bigPrimesMap.putIfAbsent(bigPrime, bSmooth);
        if (currentValue == null) {
            return false;
        }

        xor(bSmooth, currentValue);
        try {
            DataQueue.bSmooths.put(bSmooth);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        usedBigPrimesCount++;


        return true;
    }

    private void xor(BSmooth bSmooth, BSmooth currentValue) {
        bSmooth.a = currentValue.getA().multiply(bSmooth.getA());
        bSmooth.b = currentValue.getB().multiply(bSmooth.getB());
        bSmooth.vector.xor(currentValue.vector);
        bSmooth.originalVector.xor(currentValue.vector);
    }
}
