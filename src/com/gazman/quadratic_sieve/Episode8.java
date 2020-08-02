package com.gazman.quadratic_sieve;

import com.gazman.quadratic_sieve.data.BSmooth;
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

public class Episode8 extends BaseFact {

    private int B;
    private final List<Integer> primeBase = new ArrayList<>();
    private final List<Wheel2> wheels = new ArrayList<>();
    public static final BigInteger RANGE = BigInteger.valueOf(10_000);
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final GaussianEliminationMatrix3 matrix = new GaussianEliminationMatrix3();

    public static void main(String[] args) {
        new Episode8().start(140);
    }

    @Override
    protected void solve(BigInteger N) {
        B = 80_000;
        matrix.setN(N);
        buildPrimeBase(N);
        BigInteger position = N.sqrt().add(BigInteger.ONE);
        buildWheels(N, position);
        findBSmoothValues(N, position);
    }

    private void buildWheels(BigInteger N, BigInteger position) {
        for (int prime : primeBase) {
            long[] ressol = MathUtils.ressol(prime, N, position);
            for (long p : ressol) {
                if (p != -1) {
                    wheels.add(new Wheel2(prime, p));
                }
            }
        }
        log("Built wheels");
    }

    private void buildPrimeBase(BigInteger N) {
        List<Integer> primes = SieveOfEratosthenes.findPrimes(B);
        for (int prime : primes) {
            if (MathUtils.isRootInQuadraticResidues(N, BigInteger.valueOf(prime))) {
                primeBase.add(prime);
            }
        }

        log("built prime base of", primeBase.size() + "/" + B + ".", "Max prime",
                primeBase.get(primeBase.size() - 1));
    }

    private void findBSmoothValues(BigInteger N, BigInteger position) {
        double[] logs = new double[RANGE.intValue()];
        //noinspection InfiniteLoopStatement
        while (true) {
            double maxPrime = primeBase.get(primeBase.size() - 1).doubleValue();
            double baseLog = Math.log(position.pow(2).mod(N).doubleValue()) -
                    Math.log(maxPrime * 2);
            for (Wheel2 Wheel2 : wheels) {
                Wheel2.update(logs);
            }

            for (int i = 0; i < logs.length; i++) {
                if (logs[i] > baseLog) {
                    BigInteger localPosition = position.add(BigInteger.valueOf(i));
                    baseLog = localPosition.pow(2).mod(N).doubleValue();
                    executor.execute(() -> addBSmoothValue(localPosition, N));
                }
                logs[i] = 0;
            }

            position = position.add(RANGE);
        }
    }

    private void addBSmoothValue(BigInteger localPosition, BigInteger N) {
        BigInteger b = localPosition.pow(2).subtract(N);
        BitSet vector = extractVector(b);
        if (vector != null) {
            BSmooth bSmooth = new BSmooth(localPosition, b, vector);
            matrix.add(bSmooth);
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

