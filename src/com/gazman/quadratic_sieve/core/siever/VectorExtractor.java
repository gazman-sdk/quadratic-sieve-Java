package com.gazman.quadratic_sieve.core.siever;

import com.gazman.quadratic_sieve.core.matrix.Matrix;
import com.gazman.quadratic_sieve.data.*;
import com.gazman.quadratic_sieve.logger.Logger;
import com.gazman.quadratic_sieve.primes.BigPrimes;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.gazman.quadratic_sieve.QuadraticSieve.DEBUG;
import static com.gazman.quadratic_sieve.logger.Logger.VE_TEST_END;
import static com.gazman.quadratic_sieve.logger.Logger.log;

/**
 * Responsible for sieving
 */
public class VectorExtractor {

    private final VectorData bSmoothVectorData = new VectorData(null, null);
    private static final long startTimeNano = System.nanoTime();
    private static long lastUpdateNano;
    private static final AtomicInteger processed = new AtomicInteger();
    private static final AtomicInteger fullRelations = new AtomicInteger();
    private static final AtomicInteger partialRelations = new AtomicInteger();

    public void extract(PolynomialData polynomialData, List<BSmoothData> bSmoothList) {
        for (BSmoothData bSmoothData : bSmoothList) {
            extract(polynomialData, bSmoothData.localX);
        }
    }

    public void extract(PolynomialData polynomialData, long localX) {
        processed.getAndIncrement();
        VectorData vectorData = extractVector(polynomialData.getSievingValue(localX));
        if (vectorData == bSmoothVectorData) {
            fullRelations.getAndIncrement();
            BSmooth bSmooth = new BSmooth(polynomialData, localX, vectorData.vector);
            try {
                DataQueue.bSmooths.put(bSmooth);
            } catch (InterruptedException e) {
                return;
            }
            logProgress();
        } else if (vectorData != null) {
            partialRelations.getAndIncrement();
            BSmooth bSmooth = new BSmooth(polynomialData, localX, vectorData.vector);

            if (BigPrimes.instance.addBigPrime(bSmooth, vectorData.bigPrime.longValue())) {
                logProgress();
            }
        }

    }

    private static void logProgress() {
        if (!DEBUG) {
            return;
        }
        if (System.nanoTime() - lastUpdateNano > 1_000_000_000) {
            lastUpdateNano = System.nanoTime();
            int bSmoothFound = Matrix.instance.getSize();
            double totalBSmoothValues = PrimeBase.instance.primeBase.size() * 0.95;
            double progress = bSmoothFound / totalBSmoothValues;

            long timePast = System.nanoTime() - startTimeNano;
            double speedInMilliseconds = 1_000_000.0 * bSmoothFound / timePast;
            BigPrimes bigPrimes = BigPrimes.instance;
            int partialRelations = VectorExtractor.partialRelations.get();
            int fullRelations = VectorExtractor.fullRelations.get();
            int processed = VectorExtractor.processed.get();
            log("Completed", Logger.formatDouble(progress * 100, 2) + "%",
                    "extraction-rate", Logger.formatDouble((partialRelations + fullRelations) / 1.0 / processed, 2) + "%",
                    "bSmooth-found", Logger.formatLong(fullRelations),
                    "big-primes-found", Logger.formatLong(bigPrimes.getUsedBigPrimesCount()) + "/" +
                            Logger.formatLong(partialRelations),
                    Arrays.asList(Logger.values()),
                    "TTD", Logger.formatTime((long) ((totalBSmoothValues - bSmoothFound) / speedInMilliseconds)));
        }
    }

    private VectorData extractVector(BigInteger value) {
        List<BigInteger> primeBaseBigInteger = PrimeBase.instance.primeBaseBigInteger;
        int primeBaseSize = PrimeBase.instance.primeBase.size();

        BitSet vector = new BitSet(primeBaseSize);
        if (value.signum() == -1) {
            vector.set(0);
            value = value.negate();
        }
        value = extractPower2(value, vector);
        for (int i = 2; i < primeBaseSize; i++) {
            BigInteger p = primeBaseBigInteger.get(i);

            Logger.VE_DIVIDE.start();
            BigInteger[] results = value.divideAndRemainder(p);
            Logger.VE_DIVIDE.end();
            if (results[1].equals(BigInteger.ZERO)) {
                Logger.VE_POWERS.start();
                value = extractPowers(results[0], vector, i, p);
                Logger.VE_POWERS.end();

                VE_TEST_END.start();
                try {
                    if (value.equals(BigInteger.ONE)) {
                        bSmoothVectorData.vector = vector;
                        return bSmoothVectorData;
                    } else if (value.compareTo(PrimeBase.instance.maxPrimeBigInteger) <= 0) {
                        int index = PrimeBase.instance.primeBaseMap.get(value);
                        vector.set(index);
                        bSmoothVectorData.vector = vector;
                        return bSmoothVectorData;
                    } else if (value.bitLength() / 2 < p.bitLength()) {
                        Logger.VE_PRIME.start();
                        if (isPrime(value.longValue())) {
                            Logger.VE_PRIME.end();
                            return new VectorData(vector, value);
                        }
                        Logger.VE_PRIME.end();
                        return null;
                    }
                } finally {
                    VE_TEST_END.end();
                }
            }
        }
        return null;
    }

    private BigInteger extractPowers(BigInteger value, BitSet vector, int primeIndex, BigInteger prime) {
        int count = 1;
        while (true) {
            BigInteger[] results = value.divideAndRemainder(prime);
            if (!results[1].equals(BigInteger.ZERO)) {
                break;
            }
            value = results[0];
            count++;
        }
        if (count % 2 == 1) {
            vector.set(primeIndex);
        }
        return value;
    }

    private BigInteger extractPower2(BigInteger value, BitSet vector) {
        int lowestSetBit = value.getLowestSetBit();
        if (lowestSetBit > 0) {
            value = value.shiftRight(lowestSetBit);
            if (lowestSetBit % 2 == 1) {
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
