package com.gazman.quadratic_sieve;

import com.gazman.quadratic_sieve.data.BSmooth;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Episode4 extends BaseFact {

    private int B;
    private final List<BSmooth> bSmooth = new ArrayList<>();
    private final List<Integer> primeBase = new ArrayList<>();

    public static void main(String[] args) {
        new Episode4().start(70);
    }

    @Override
    protected void solve(BigInteger N) {
        B = 100;
        buildVectorBase();
        findBSmoothValues(N);
    }

    private void buildVectorBase() {
        BigInteger p = BigInteger.valueOf(2);
        for (int i = 0; i < B; i++) {
            primeBase.add(p.intValue());
            p = p.nextProbablePrime();
        }
    }

    private void findBSmoothValues(BigInteger N) {
        for (int k = 1; ; k++) {
            BigInteger sqrt = N.multiply(BigInteger.valueOf(k)).sqrt();
            for (int i = 0; i < 100; i++) {
                BigInteger a = sqrt.add(BigInteger.valueOf(i));
                BigInteger mod = a.pow(2).mod(N);

                BitSet vector = isSmooth(mod);
                if (vector != null) {
                    bSmooth.add(new BSmooth(a, mod, vector));
                    if (bSmooth.size() > B) {
                        log("BSmooth values found");
                        return;
                    }
                }
            }
        }

    }

    private BitSet isSmooth(BigInteger value) {
        for (int i = 0; i < primeBase.size(); i++) {
            int prime = primeBase.get(i);
            BigInteger p = BigInteger.valueOf(prime);
            int count = 0;
            BitSet vector = new BitSet();
            if (value.mod(p).equals(BigInteger.ZERO)) {
                while (value.mod(p).equals(BigInteger.ZERO)) {
                    value = value.divide(p);
                    count++;
                }
                vector.set(i, count % 2 == 0);
                if (value.equals(BigInteger.ONE)) {
                    return vector;
                }
            }
        }
        return null;
    }
}
