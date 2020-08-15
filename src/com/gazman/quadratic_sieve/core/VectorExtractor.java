package com.gazman.quadratic_sieve.core;

import com.gazman.quadratic_sieve.data.*;
import com.gazman.quadratic_sieve.core.matrix.*;
import com.gazman.quadratic_sieve.logger.Logger;
import com.gazman.quadratic_sieve.primes.BigPrimes;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;

import static com.gazman.quadratic_sieve.logger.Logger.log;

/**
 * Responsible for sieving
 */
public class VectorExtractor implements Runnable{

    private final VectorData bSmoothVectorData = new VectorData(null, null);
    private static final long startTimeNano = System.nanoTime();
    private static long lastUpdateNano;

    public void start() {
        new Thread(this, "VectorExtractor").start();
    }

    @Override
    public void run() {
        while (true) {
            VectorWorkData workData;
            try {
                workData = DataQueue.vectorWorkData.take();
            } catch (InterruptedException e) {
                return;
            }

            Logger.VECTOR_EXTRACTOR.start();
            VectorData vectorData = extractVector(workData.polynomialData.getSievingValue(workData.localX, workData.c));
            Logger.VECTOR_EXTRACTOR.end();
            if (vectorData == bSmoothVectorData) {
                BSmooth bSmooth = new BSmooth(workData.polynomialData, workData.localX, vectorData.vector);
                try {
                    DataQueue.bSmooths.put(bSmooth);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                logProgress();
            } else if (vectorData != null) {
                BSmooth bSmooth = new BSmooth(workData.polynomialData, workData.localX, vectorData.vector);

                if (BigPrimes.instance.addBigPrime(bSmooth, vectorData.bigPrime)) {
                    logProgress();
                }
            }
        }
    }

    private static void logProgress() {
        if (System.nanoTime() - lastUpdateNano > 1_000_000_000) {
            lastUpdateNano = System.nanoTime();
            int bSmoothFound = Matrix.instance.getSize();
            double totalBSmoothValues = PrimeBase.instance.primeBase.size() * 0.95;
            double progress = bSmoothFound / totalBSmoothValues;

            long timePast = System.nanoTime() - startTimeNano;
            double speedInMilliseconds = 1_000_000.0 * bSmoothFound / timePast;
            BigPrimes bigPrimes = BigPrimes.instance;
            log("Completed", Logger.formatDouble(progress * 100, 2) + "%",
                    "bSmooth-found", Logger.formatLong(bSmoothFound - bigPrimes.getUsedBigPrimesCount()),
                    "big-primes-found", Logger.formatLong(bigPrimes.getUsedBigPrimesCount()) + "/" +
                            Logger.formatLong(bigPrimes.getTotalBigPrimes()),
                    Arrays.asList(Logger.values()),
                    "TTD", Logger.formatTime((long) ((totalBSmoothValues - bSmoothFound) / speedInMilliseconds)));
        }
    }

    private VectorData extractVector(BigInteger value) {
        BitSet vector = new BitSet(PrimeBase.instance.primeBase.size());
        if(value.compareTo(BigInteger.ZERO) < 0){
            vector.set(0);
            value = value.abs();
        }
        for (int i = 1; i < PrimeBase.instance.primeBase.size(); i++) {
            BigInteger p = PrimeBase.instance.primeBaseBigInteger.get(i);
            int count = 1;

            BigInteger[] results = value.divideAndRemainder(p);
            if (results[1].equals(BigInteger.ZERO)) {
                value = results[0];
                while (true) {
                    results = value.divideAndRemainder(p);
                    if(!results[1].equals(BigInteger.ZERO)){
                        break;
                    }
                    value = results[0];
                    count++;
                }
                vector.set(i, count % 2 == 1);

                if (value.equals(BigInteger.ONE)) {
                    bSmoothVectorData.vector = vector;
                    return bSmoothVectorData;
                } else if (value.compareTo(PrimeBase.instance.maxPrimeBigInteger) <= 0) {
                    int index = PrimeBase.instance.primeBaseMap.get(value);
                    vector.set(index);
                    bSmoothVectorData.vector = vector;
                    return bSmoothVectorData;
                } else if (value.bitLength() / 2 < p.bitLength()) {
                    if (isPrime(value.longValue())) {
                        return new VectorData(vector, value);
                    }
                    return null;
                }
            }
        }
        return null;
    }

    private boolean isPrime(long n) {
        long sqrtN = (long) Math.sqrt(n) + 1;
        int[] mods = {1, 7, 11, 13, 17, 19, 23, 29};
        for (long i = PrimeBase.instance.maxPrime - PrimeBase.instance.maxPrime % 30; i <= sqrtN; i += 30) {
            for (int mod : mods) {
                if (n % (i + mod) == 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
