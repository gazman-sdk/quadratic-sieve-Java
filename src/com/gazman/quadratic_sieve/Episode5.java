package com.gazman.quadratic_sieve;

import com.gazman.quadratic_sieve.data.BSmooth;
import com.gazman.quadratic_sieve.matrix.GaussianEliminationMatrix;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Episode5 extends BaseFact {

    private int B;
    private final List<BSmooth> bSmooth = new ArrayList<>();
    private final List<BigInteger> primeBase = new ArrayList<>();

    public static void main(String[] args) {
        new Episode5().start(70);
    }

    @Override
    protected void solve(BigInteger N) {
        B = 100;
        buildVectorBase();
        findBSmoothValues(N);
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

    private void buildVectorBase() {
        BigInteger p = BigInteger.valueOf(2);
        for (int i = 0; i < B; i++) {
            primeBase.add(p);
            p = p.nextProbablePrime();
        }
    }

    private void findBSmoothValues(BigInteger N) {
        BigInteger sqrt = N.sqrt();
        for (int k = 1; ; k++) {
            BigInteger a = sqrt.add(BigInteger.valueOf(k));
            BigInteger mod = a.pow(2).mod(N);

            BitSet vector = extractVector(mod);
            if (vector != null) {
                bSmooth.add(new BSmooth(a, mod, vector));
                if (bSmooth.size() > B) {
                    log("BSmooth values found");
                    return;
                }
            }
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

