package com.gazman.quadratic_sieve.core.siever;

import com.gazman.quadratic_sieve.core.matrix.Matrix;
import com.gazman.quadratic_sieve.data.*;
import com.gazman.quadratic_sieve.logger.Logger;
import com.gazman.quadratic_sieve.primes.BigPrimes;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import static com.gazman.quadratic_sieve.logger.Logger.log;

/**
 * Responsible for sieving
 */
public class VectorExtractor {

    private final VectorData bSmoothVectorData = new VectorData(null, null);
    private static final long startTimeNano = System.nanoTime();
    private static long lastUpdateNano;

    public void extract(PolynomialData polynomialData, List<BSmoothData> bSmoothList) {
        for (BSmoothData bSmoothData : bSmoothList) {
            extract(polynomialData, bSmoothData.localX);
        }
    }

    public void extract(PolynomialData polynomialData, long localX) {
        Logger.VECTOR_EXTRACTOR.start();
        VectorData vectorData = extractVector(polynomialData.getSievingValue(localX));
        Logger.VECTOR_EXTRACTOR.end();
        if (vectorData == bSmoothVectorData) {
            BSmooth bSmooth = new BSmooth(polynomialData, localX, vectorData.vector);
            try {
                DataQueue.bSmooths.put(bSmooth);
            } catch (InterruptedException e) {

                return;
            }
            logProgress();
        } else if (vectorData != null) {
            BSmooth bSmooth = new BSmooth(polynomialData, localX, vectorData.vector);

            if (BigPrimes.instance.addBigPrime(bSmooth, vectorData.bigPrime.longValue())) {
                logProgress();
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
        value = extractPower2(value, vector);
        for (int i = 2; i < PrimeBase.instance.primeBase.size(); i++) {
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
                if(count % 2 == 1) {
                    vector.set(i);
                }

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

    private BigInteger extractPower2(BigInteger value, BitSet vector) {
        int lowestSetBit = value.getLowestSetBit();
        if(lowestSetBit > 0){
            value = value.shiftRight(lowestSetBit);
            if(lowestSetBit % 2 == 1){
                vector.set(1);
            }
        }
        return value;
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
