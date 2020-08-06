package com.gazman.quadratic_sieve;

import com.gazman.quadratic_sieve.data.BSmooth;
import com.gazman.quadratic_sieve.logger.Logger;
import com.gazman.quadratic_sieve.matrix.GaussianEliminationMatrix3;
import com.gazman.quadratic_sieve.primes.SieveOfEratosthenes;
import com.gazman.quadratic_sieve.utils.MathUtils;
import com.gazman.quadratic_sieve.wheel.Wheel2;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Episode10 extends BaseFact {

    public static int loopsCount;
    public static BigInteger loopsSize;
    private int B;
    private final List<Integer> primeBase = new ArrayList<>();
    private final List<Wheel2> wheels = new ArrayList<>();
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final GaussianEliminationMatrix3 matrix = new GaussianEliminationMatrix3();
    private long startTimeNano, lastUpdateNano;
    private int minPrimeSize;
    private int maxPrimeThreshold;

    public static void main(String[] args) {
        new Episode10().start(200);
    }

    @Override
    protected void solve(BigInteger N) {
        log();
        log("Magic numbers");
        log();

        B = (int) (234.375 * Math.pow(1.0356, N.bitLength()));
        minPrimeSize = (int) Math.pow(Math.log(N.doubleValue()), 0.9);
        loopsCount = (int) (Math.sqrt(B));
        loopsSize = BigInteger.valueOf(B / 4);
        log("B-bound", Logger.formatLong(B));
        log("minPrimeSize", Logger.formatLong(minPrimeSize));
        log("loopsCount", Logger.formatLong(loopsCount));
        log("loopsSize", Logger.formatLong(loopsSize.longValue()));
        buildPrimeBase(N);
        maxPrimeThreshold = (int) (primeBase.get(primeBase.size() - 1) * 0.1);

        log("maxPrimeThreshold", Logger.formatLong(maxPrimeThreshold));
        log();

        matrix.setN(N);

        BigInteger q = N.multiply(BigInteger.TWO).sqrt().divide(loopsSize.multiply(BigInteger.valueOf(loopsCount))).sqrt();
        startTimeNano = lastUpdateNano = System.nanoTime();
        //noinspection InfiniteLoopStatement
        while (true) {
            Logger.CHOOSING_POLYNOMIAL.start();
            do {
                q = q.nextProbablePrime();
            } while (!MathUtils.isRootInQuadraticResidues(N, q));

            BigInteger aRoot = MathUtils.modSqrt(N, q);
            BigInteger a = q.pow(2);
            BigInteger k = N.subtract(aRoot.pow(2)).divide(q).multiply(BigInteger.TWO.multiply(aRoot).modInverse(q)).mod(q);
            BigInteger b = k.multiply(q).add(aRoot);

            buildWheels(N, a, b);
            Logger.CHOOSING_POLYNOMIAL.end();
            Logger.SIEVING.start();
            findBSmoothValues(N, a, b);
            Logger.SIEVING.end();
        }
    }


    private void buildWheels(BigInteger N, BigInteger a, BigInteger b) {
        wheels.clear();
        for (int p : primeBase) {
            if (p < minPrimeSize) {
                continue;
            }
            BigInteger prime = BigInteger.valueOf(p);
            BigInteger root = MathUtils.modSqrt(N, prime);
            BigInteger p1 = root.subtract(b).multiply(a.modInverse(prime)).mod(prime);
            BigInteger p2 = prime.subtract(root).subtract(b).multiply(a.modInverse(prime)).mod(prime);
            wheels.add(new Wheel2(p, p1.intValue()));
            wheels.add(new Wheel2(p, p2.intValue()));
        }
    }

    private void buildPrimeBase(BigInteger N) {
        List<Integer> primes = SieveOfEratosthenes.findPrimes(B);
        for (int prime : primes) {
            if (MathUtils.isRootInQuadraticResidues(N, BigInteger.valueOf(prime))) {
                primeBase.add(prime);
            }
        }

        log("Prime-base", Logger.formatLong(primeBase.size()));
        log("Max-prime", Logger.formatLong(primeBase.get(primeBase.size() - 1)));
    }

    private void findBSmoothValues(BigInteger N, BigInteger a, BigInteger b) {
        double[] logs = new double[loopsSize.intValue()];
        double maxPrime = primeBase.get(primeBase.size() - 1).doubleValue();
        BigInteger x = BigInteger.ZERO;


        BigInteger ac = b.pow(2).subtract(N);
        BigInteger c = ac.divide(a);

        for (int j = 0; j < loopsCount; j++) {
            double baseLog = Math.log(a.multiply(x.pow(2)).add(b.multiply(x).multiply(BigInteger.TWO).add(c)).doubleValue()) -
                    Math.log(maxPrime * maxPrimeThreshold);
            for (Wheel2 wheel : wheels) {
                wheel.update(logs);
            }

            for (int i = 0; i < logs.length; i++) {
                if (logs[i] > baseLog) {
                    BigInteger localX = x.add(BigInteger.valueOf(i));
                    executor.execute(() -> addBSmoothValue(
                            a.multiply(localX.pow(2)).add(b.multiply(localX).multiply(BigInteger.TWO).add(c)),
                            a.multiply(localX).add(b),
                            a.multiply(localX).add(b).pow(2).subtract(N)));
                }
                logs[i] = 0;
            }

            x = x.add(loopsSize);
        }
    }

    private void addBSmoothValue(BigInteger sievingValue, BigInteger a, BigInteger mod) {
        Logger.EXTRACTING_VECTOR.start();
        BitSet vector = extractVector(sievingValue);
        Logger.EXTRACTING_VECTOR.end();
        if (vector != null) {
            BSmooth bSmooth = new BSmooth(a, mod, vector);
            Logger.MATRIX.start();
            matrix.add(bSmooth);
            Logger.MATRIX.end();
            logProgress();
        }
    }

    private void logProgress() {
        if (System.nanoTime() - lastUpdateNano > 1_000_000_000) {
            lastUpdateNano = System.nanoTime();
            int bSmoothFound = matrix.getSize();
            double totalBSmoothValues = primeBase.size() * 0.95;
            double progress = bSmoothFound / totalBSmoothValues;

            long timePast = System.nanoTime() - startTimeNano;
            double speedInMilliseconds = 1_000_000.0 * bSmoothFound / timePast;
            log("Completed", Logger.formatDouble(progress * 100, 2) + "%",
                    "found", Logger.formatLong(bSmoothFound),
                    Logger.CHOOSING_POLYNOMIAL,
                    Logger.SIEVING,
                    Logger.EXTRACTING_VECTOR,
                    Logger.MATRIX,
                    "TTD", Logger.formatTime((long) ((totalBSmoothValues - bSmoothFound) / speedInMilliseconds)));
        }
    }

    private BitSet extractVector(BigInteger value) {
        BitSet vector = new BitSet();
        for (int i = 0; i < primeBase.size(); i++) {
            BigInteger p = BigInteger.valueOf(primeBase.get(i));
            int count = 0;
            if (value.mod(p).equals(BigInteger.ZERO)) {
                while (value.mod(p).equals(BigInteger.ZERO)) {
                    value = value.divide(p);
                    count++;
                }
                vector.set(i, count % 2 == 1);
                if (value.equals(BigInteger.ONE)) {
                    return vector;
                }
            }
        }
        return null;
    }

}

