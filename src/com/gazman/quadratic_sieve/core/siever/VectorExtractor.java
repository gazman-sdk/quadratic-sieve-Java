package com.gazman.quadratic_sieve.core.siever;

import com.gazman.quadratic_sieve.core.matrix.Matrix;
import com.gazman.quadratic_sieve.data.BSmooth;
import com.gazman.quadratic_sieve.data.DataQueue;
import com.gazman.quadratic_sieve.data.PolynomialData;
import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.logger.Analytics;
import com.gazman.quadratic_sieve.logger.Logger;
import com.gazman.quadratic_sieve.primes.BigPrimes;

import java.util.Arrays;
import java.util.List;

import static com.gazman.quadratic_sieve.QuadraticSieve.DEBUG;
import static com.gazman.quadratic_sieve.logger.Logger.log;

/**
 * Responsible for sieving
 */
public class VectorExtractor {

    private static final long startTimeNano = System.nanoTime();
    private static long lastUpdateNano;

    public void extract(PolynomialData polynomialData, List<BSmoothData> bSmoothList) {
        for (BSmoothData bSmoothData : bSmoothList) {
            if (bSmoothData.reminder < 0) {
                bSmoothData.vector.set(0);
                bSmoothData.reminder *= -1;
            }
            int powerTwo = Long.numberOfTrailingZeros(bSmoothData.reminder);
            if ((powerTwo & 0x1) == 1) {
                bSmoothData.vector.set(1);
            }
            bSmoothData.reminder >>= powerTwo;

            List<Integer> primeBase = PrimeBase.instance.primeBase;
            for (int i = 2; i < primeBase.size(); i++) {
                int prime = primeBase.get(i);
                if (prime > bSmoothData.reminder) {
                    break;
                }
                if (bSmoothData.reminder % prime == 0) {
                    int count = 0;
                    do {
                        bSmoothData.reminder /= prime;
                        count++;
                    } while (bSmoothData.reminder % prime == 0);
                    if ((count & 0x1) == 1) {
                        bSmoothData.vector.flip(i);
                    }
                }
            }
            if (bSmoothData.reminder == 1) {
                BSmooth bSmooth = new BSmooth(polynomialData, bSmoothData.localX, bSmoothData.vector);
                try {
                    DataQueue.bSmooths.put(bSmooth);
                } catch (InterruptedException e) {
                    return;
                }
                logProgress();
            } else {
                BSmooth bSmooth = new BSmooth(polynomialData, bSmoothData.localX, bSmoothData.vector);

                if (BigPrimes.instance.addBigPrime(bSmooth, bSmoothData.reminder)) {
                    logProgress();
                }
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
            log("Completed", Logger.formatDouble(progress * 100, 2) + "%",
                    "bSmooth-found", Logger.formatLong(bSmoothFound - bigPrimes.getUsedBigPrimesCount()),
                    "big-primes-found", Logger.formatLong(bigPrimes.getUsedBigPrimesCount()) + "/" +
                            Logger.formatLong(bigPrimes.getTotalBigPrimes()),
                    Arrays.asList(Analytics.values()),
                    "TTD", Logger.formatTime((long) ((totalBSmoothValues - bSmoothFound) / speedInMilliseconds)));
        }
    }
}
