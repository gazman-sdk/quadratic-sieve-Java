package com.gazman.quadratic_sieve.core.siever;

import com.gazman.quadratic_sieve.core.matrix.Matrix;
import com.gazman.quadratic_sieve.data.*;
import com.gazman.quadratic_sieve.logger.Logger;
import com.gazman.quadratic_sieve.primes.BigPrimes;
import com.gazman.quadratic_sieve.wheel.Wheel;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import static com.gazman.quadratic_sieve.logger.Logger.log;

/**
 * Responsible for sieving
 */
public class VectorExtractor {

    private static final long startTimeNano = System.nanoTime();
    private static long lastUpdateNano;
    private static final double MAX_REMINDER = Math.log(Long.MAX_VALUE);
    private static final double MINIMUM_LOG = 0.001;


    public void extract(PolynomialData polynomialData, List<BSmoothData> bSmoothDataList) {
        Logger.VECTOR_EXTRACTOR.start();
        List<Wheel> wheels = polynomialData.wheels;
        calculateReminder(polynomialData, bSmoothDataList);

        Logger.TEST.start();
        for (Wheel wheel : wheels) {
            wheel.update(bSmoothDataList);
        }
        Logger.TEST.end();

        for (BSmoothData bSmoothData : bSmoothDataList) {
            extract(polynomialData, bSmoothData);
        }
        Logger.VECTOR_EXTRACTOR.end();
    }

    private void extract(PolynomialData polynomialData, BSmoothData bSmoothData) {
        long reminder = bSmoothData.reminder;
        if (reminder == -1) {
            return;
        }
        BitSet vector = bSmoothData.vector;

        if(bSmoothData.negative){
            vector.set(0);
        }

        List<Integer> primeBase = PrimeBase.instance.primeBase;
        if (reminder == 0) {
            addBSmooth(polynomialData, bSmoothData, vector);
            return;
        }
        reminder = calculatePrimePowers(reminder, vector, primeBase);
        if (reminder == 1) {
            addBSmooth(polynomialData, bSmoothData, vector);
            return;
        }
        reminder = extractSmallPrimes(reminder, vector, primeBase);
        if(reminder == 1){
            addBSmooth(polynomialData, bSmoothData, vector);
            return;
        }
        if (!isPrime(reminder)) {
            return;
        }

        if (BigPrimes.instance.addBigPrime(new BSmooth(polynomialData, bSmoothData.localX, vector), reminder)) {
            logProgress();
        }
    }

    private long extractSmallPrimes(long reminder, BitSet vector, List<Integer> primeBase) {
        for (int i = 1; i < primeBase.size(); i++) {
            int prime = primeBase.get(i);
            if(prime >= MagicNumbers.instance.minPrimeSize){
                return reminder;
            }
            int count = 0;
            while (reminder % prime == 0){
                reminder /= prime;
                count++;
            }
            if(count % 2 == 1){
                vector.set(i);
            }
        }
        return reminder;
    }

    private void addBSmooth(PolynomialData polynomialData, BSmoothData bSmoothData, BitSet vector) {
        try {
            DataQueue.bSmooths.put(new BSmooth(polynomialData, bSmoothData.localX, vector));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logProgress();
    }

    private long calculatePrimePowers(long reminder, BitSet vector, List<Integer> primeBase) {
        for (int i = vector.previousSetBit(vector.size()); i >= 0; i = vector.previousSetBit(i - 1)) {
            int prime = primeBase.get(i);
            int extras = 0;
            while (reminder % prime == 0) {
                reminder = reminder / prime;
                extras++;
            }
            if (extras % 2 == 1) {
                vector.clear(i);
            }
        }
        return reminder;
    }

    private void calculateReminder(PolynomialData polynomialData, List<BSmoothData> bSmoothDataList) {
        for (BSmoothData bSmoothData : bSmoothDataList) {
            BigInteger b = polynomialData.getSievingValue(bSmoothData.localX);
            bSmoothData.negative = b.compareTo(BigInteger.ZERO) < 0;
            double trueLog = Math.log(b.doubleValue());
            double remainingLog = trueLog - bSmoothData.log;
            if (remainingLog < MINIMUM_LOG) {
                bSmoothData.reminder = 0;
            } else if (remainingLog < MAX_REMINDER) {
                bSmoothData.reminder = Math.round(Math.exp(remainingLog));
            } else {
                bSmoothData.reminder = -1;
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
