package com.gazman.quadratic_sieve.data;

import com.gazman.quadratic_sieve.debug.Logger;

import java.math.BigInteger;

import static com.gazman.quadratic_sieve.debug.Logger.log;

public class MagicNumbers {

    public int minPrimeSize;
    public int loopsCount;
    public int loopsSize;
    public int maxPrimeThreshold;
    public int primeBaseSize;
    public double longRoundingError;

    public static final MagicNumbers instance = new MagicNumbers();

    public void initN(BigInteger N) {
        primeBaseSize = (int) (6.71 * Math.pow(1.0356, N.bitLength()));
    }

    public void init() {
        maxPrimeThreshold = (int) (primeBaseSize * 1.01);
        minPrimeSize = (int) (primeBaseSize * 0.008);
        loopsSize = primeBaseSize * 5;
        loopsCount = (int) (Math.sqrt(loopsSize));
        longRoundingError = loopsCount;

        log("primeBaseSize", primeBaseSize);
        log("minPrimeSize", Logger.formatLong(minPrimeSize));
        log("loopsCount", Logger.formatLong(loopsCount));
        log("loopsSize", Logger.formatLong(loopsSize));
        log("maxPrimeThreshold", maxPrimeThreshold);
    }
}
