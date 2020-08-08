package com.gazman.quadratic_sieve.primes;

import com.gazman.quadratic_sieve.data.BSmooth;
import com.gazman.quadratic_sieve.matrix.GaussianEliminationMatrix4;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.function.Function;

public class BigPrimes {

    public static final BigPrimes instance = new BigPrimes();

    private final HashMap<BigInteger, BSmooth> bigPrimesMap = new HashMap<>();
    private int totalBigPrimes;
    private int usedBigPrimesCount;

    public int getTotalBigPrimes() {
        return totalBigPrimes;
    }

    public int getUsedBigPrimesCount() {
        return usedBigPrimesCount;
    }

    public boolean addBigPrime(BSmooth bSmooth, BigInteger bigPrime, GaussianEliminationMatrix4 matrix) {
        totalBigPrimes++;
        BSmooth currentValue = bigPrimesMap.putIfAbsent(bigPrime, bSmooth);
        if (currentValue == null) {
            return false;
        }

        xor(bSmooth, currentValue);
        matrix.add(bSmooth);
        usedBigPrimesCount++;


        return true;
    }

    private void xor(BSmooth bSmooth, BSmooth currentValue) {
        bSmooth.a = currentValue.getA().multiply(bSmooth.getA());
        bSmooth.b = currentValue.getB().multiply(bSmooth.getB());
        bSmooth.vector.xor(currentValue.vector);
    }
}
