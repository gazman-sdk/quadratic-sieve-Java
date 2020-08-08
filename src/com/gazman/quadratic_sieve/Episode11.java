package com.gazman.quadratic_sieve;

import com.gazman.quadratic_sieve.data.BSmooth;
import com.gazman.quadratic_sieve.data.PolynomialData;
import com.gazman.quadratic_sieve.data.VectorData;
import com.gazman.quadratic_sieve.logger.Logger;
import com.gazman.quadratic_sieve.matrix.GaussianEliminationMatrix4;
import com.gazman.quadratic_sieve.primes.BigPrimes;
import com.gazman.quadratic_sieve.primes.SieveOfEratosthenes;
import com.gazman.quadratic_sieve.utils.MathUtils;
import com.gazman.quadratic_sieve.wheel.Wheel2;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Episode11 extends BaseFact {

    public static int loopsCount;
    public static BigInteger loopsSize;
    private int B;
    private final List<Integer> primeBase = new ArrayList<>();
    private final Map<BigInteger, Integer> primeBaseMap = new HashMap<>();
    private final List<Wheel2> wheels = new ArrayList<>();
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final GaussianEliminationMatrix4 matrix = new GaussianEliminationMatrix4();
    private long startTimeNano, lastUpdateNano;
    private int minPrimeSize;
    private int maxPrimeThreshold;
    private Integer maxPrime;
    private BigInteger maxPrimeBigInteger;
    private final VectorData bSmoothVectorData = new VectorData(null, null);

    public static void main(String[] args) {
        new Episode11().start(140);
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
            findBSmoothValues(new PolynomialData(a, b, N));
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
            BigInteger p = BigInteger.valueOf(prime);
            if (MathUtils.isRootInQuadraticResidues(N, p)) {
                primeBaseMap.put(p, primeBase.size());
                primeBase.add(prime);
            }
        }

        log("Prime-base", Logger.formatLong(primeBase.size()));
        maxPrime = primeBase.get(primeBase.size() - 1);
        maxPrimeBigInteger = BigInteger.valueOf(maxPrime);
        log("Max-prime", Logger.formatLong(maxPrime));
    }

    private void findBSmoothValues(PolynomialData polynomialData) {
        double[] logs = new double[loopsSize.intValue()];
        double maxPrime = this.maxPrime.doubleValue();
        BigInteger x = BigInteger.ZERO;


        BigInteger c = polynomialData.getC();

        Logger.CHOOSING_POLYNOMIAL.end();
        Logger.SIEVING.start();
        for (int j = 0; j < loopsCount; j++) {
            double baseLog = Math.log(polynomialData.getSievingValue(x, c).doubleValue()) -
                    Math.log(maxPrime * maxPrimeThreshold);
            for (Wheel2 wheel : wheels) {
                wheel.update(logs);
            }

            for (int i = 0; i < logs.length; i++) {
                if (logs[i] > baseLog || Math.abs(logs[i] - baseLog) < 0.0001) {
                    BigInteger localX = x.add(BigInteger.valueOf(i));
                    executor.execute(() -> addBSmoothValue(
                            polynomialData, localX, c));
                }
                logs[i] = 0;
            }

            x = x.add(loopsSize);
        }
        Logger.SIEVING.end();
    }

    private void addBSmoothValue(PolynomialData polynomialData, BigInteger localX, BigInteger c) {
        Logger.EXTRACTING_VECTOR.start();
        VectorData vectorData = extractVector(polynomialData.getSievingValue(localX, c));
        Logger.EXTRACTING_VECTOR.end();
        if (vectorData == bSmoothVectorData) {
            BSmooth bSmooth = new BSmooth(polynomialData, localX, vectorData.vector);
            matrix.add(bSmooth);
            logProgress();
        } else if (vectorData != null) {
            BSmooth bSmooth = new BSmooth(polynomialData, localX, vectorData.vector);

            if (BigPrimes.instance.addBigPrime(bSmooth, vectorData.bigPrime, matrix)) {
                logProgress();
            }
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
            BigPrimes bigPrimes = BigPrimes.instance;
            log("Completed", Logger.formatDouble(progress * 100, 2) + "%",
                    "bSmooth-found", Logger.formatLong(bSmoothFound - bigPrimes.getUsedBigPrimesCount()),
                    "big-primes-found", Logger.formatLong(bigPrimes.getUsedBigPrimesCount()) + "/" +
                            Logger.formatLong(bigPrimes.getTotalBigPrimes()),
                    Logger.CHOOSING_POLYNOMIAL,
                    Logger.SIEVING,
                    Logger.EXTRACTING_VECTOR,
                    Logger.MATRIX,
                    "TTD", Logger.formatTime((long) ((totalBSmoothValues - bSmoothFound) / speedInMilliseconds)));
        }
    }

    private VectorData extractVector(BigInteger value) {
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
                    bSmoothVectorData.vector = vector;
                    return bSmoothVectorData;
                } else if (value.compareTo(maxPrimeBigInteger) <= 0) {
                    int index = primeBaseMap.get(value);
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
        for (long i = maxPrime - maxPrime % 30; i <= sqrtN; i += 30) {
            for (int mod : mods) {
                if (n % (i + mod) == 0) {
                    return false;
                }
            }
        }
        return true;
    }


}

