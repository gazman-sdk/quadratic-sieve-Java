package com.gazman.quadratic_sieve;

import com.gazman.quadratic_sieve.data.BSmooth;
import com.gazman.quadratic_sieve.matrix.GaussianEliminationMatrix;
import com.gazman.quadratic_sieve.utils.MathUtils;
import com.gazman.quadratic_sieve.wheel.Wheel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Episode6 extends BaseFact {

    private int B;
    private List<BSmooth> bSmooth = new ArrayList<>();
    private final List<BigInteger> primeBase = new ArrayList<>();
    private final List<Wheel> wheels = new ArrayList<>();

    public static void main(String[] args) {
        new Episode6().start(130);
    }

    @Override
    protected void solve(BigInteger N) {
        B = 5000;
        buildPrimeBase(N);
        BigInteger position = N.sqrt().add(BigInteger.ONE);
        buildWheels(N, position);
        findBSmoothValues(N, position);
        log("found", bSmooth.size(), "bSmooth values");
        extractVectors();
        List<List<BSmooth>> solutions = new GaussianEliminationMatrix().solve(bSmooth, primeBase);

        log("Found", solutions.size());

        for (int i = 0; i < solutions.size(); i++) {
            List<BSmooth> solution = solutions.get(i);
            BigInteger a = BigInteger.ONE;
            BigInteger b = BigInteger.ONE;

            for (BSmooth smooth : solution) {
                a = smooth.a.multiply(a);
                b = smooth.b.multiply(b);
            }


            BigInteger maybeSolution = a.add(b.sqrt()).gcd(N);
            if (!maybeSolution.equals(N) && !maybeSolution.equals(BigInteger.ONE)) {
                log(i, "Oh yeah", maybeSolution);
                return;
            } else {
                log(i, "bad luck", maybeSolution);
            }
        }
    }

    private void extractVectors() {
        List<BSmooth> bSmooth = new ArrayList<>();
        for (BSmooth smooth : this.bSmooth) {
            BitSet vector = extractVector(smooth.b);
            if (vector != null) {
                smooth.vector = vector;
                bSmooth.add(smooth);
            }
        }
        log("Extracted", bSmooth.size() + "/" + this.bSmooth.size(), "vectors");

        this.bSmooth = bSmooth;

    }

    private void buildWheels(BigInteger N, BigInteger position) {
        for (BigInteger prime : primeBase) {
            long[] ressol = MathUtils.ressol(prime.longValue(), N, position);
            for (long p : ressol) {
                if(p != -1) {
                    wheels.add(new Wheel(prime, p));
                }
            }
        }
    }

    private void buildPrimeBase(BigInteger N) {
        BigInteger p = BigInteger.valueOf(2);
        for (int i = 0; i < B; i++) {
            if (MathUtils.isRootInQuadraticResidues(N, p)) {
                primeBase.add(p);
            }
            p = p.nextProbablePrime();
        }

        log("built prime base of", primeBase.size() + "/" + B + ".", "Max prime",
                primeBase.get(primeBase.size() - 1));
    }

    private void findBSmoothValues(BigInteger N, BigInteger position) {
        BigInteger range = BigInteger.valueOf(10_000);
        double[] logs = new double[range.intValue()];
        while (true) {
            double baseLog = Math.log(position.pow(2).mod(N).doubleValue()) -
                    Math.log(primeBase.get(primeBase.size() - 1).doubleValue());
            for (Wheel wheel : wheels) {
                wheel.update(logs);
            }

            for (int i = 0; i < logs.length; i++) {
                if (logs[i] > baseLog) {
                    BigInteger localPosition = position.add(BigInteger.valueOf(i));
                    baseLog = localPosition.pow(2).mod(N).doubleValue();
                    bSmooth.add(new BSmooth(localPosition, localPosition.pow(2).subtract(N), null));
                    if (bSmooth.size() > B) {
                        return;
                    }
                }
                logs[i] = 0;
            }

            position = position.add(range);
        }
    }

    private BitSet extractVector(BigInteger value) {
        BitSet vector = new BitSet();
        for (int i = 0; i < primeBase.size(); i++) {
            BigInteger p = primeBase.get(i);
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

