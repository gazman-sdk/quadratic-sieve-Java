package com.gazman.quadratic_sieve.data;

import com.gazman.quadratic_sieve.logger.Logger;

import java.math.BigInteger;

import static com.gazman.quadratic_sieve.logger.Logger.log;

public class MagicNumbers {


    public int B;
    public int minPrimeSize;
    public int loopsCount;
    public BigInteger loopsSize;
    public int maxPrimeThreshold;

    public static final MagicNumbers instance = new MagicNumbers();

    public void init(BigInteger N){
        B = (int) (234.375 * Math.pow(1.0356, N.bitLength()));
        minPrimeSize = (int) Math.pow(Math.log(N.doubleValue()), 0.9);
        loopsCount = (int) (Math.sqrt(B));
        loopsSize = BigInteger.valueOf(B / 4);
        log("B-bound", Logger.formatLong(B));
        log("minPrimeSize", Logger.formatLong(minPrimeSize));
        log("loopsCount", Logger.formatLong(loopsCount));
        log("loopsSize", Logger.formatLong(loopsSize.longValue()));
    }

    public void initMaxPrimeThreshold(int maxPrime){
        maxPrimeThreshold = (int) (maxPrime * 0.1);
        log("maxPrimeThreshold", maxPrimeThreshold);
    }
}
