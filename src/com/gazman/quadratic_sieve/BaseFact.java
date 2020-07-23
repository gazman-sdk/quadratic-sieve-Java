package com.gazman.quadratic_sieve;

import java.math.BigInteger;
import java.util.Random;

public abstract class BaseFact {
    private static final long startTime = System.nanoTime();

    protected void start(int bitLLength) {
        bitLLength /=2;
        Random random = new Random(123);

        BigInteger A = new BigInteger(bitLLength - 1, random).nextProbablePrime();
        BigInteger B = new BigInteger(bitLLength + 1, random).nextProbablePrime();
        BigInteger N = A.multiply(B);

        log("A", A);
        log("B", B);
        log("N", N);

        solve(N);
    }

    protected abstract void solve(BigInteger n);

    protected void log(Object... objects) {
        StringBuilder out = new StringBuilder();
        for (Object object : objects) {
            out.append(object).append(" ");
        }

        long milliseconds = (System.nanoTime() - startTime) / 1_000_000;

        String prefix = milliseconds + "> ";
        System.out.println(prefix + out);
    }
}
