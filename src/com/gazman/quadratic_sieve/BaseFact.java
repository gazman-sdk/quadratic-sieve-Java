package com.gazman.quadratic_sieve;

import java.math.BigInteger;
import java.util.Random;

public abstract class BaseFact {
    private static final long startTime = System.nanoTime();
    private boolean logsAvailable = true;
    private final Random random = new Random(123);

    protected void start(int bitLength) {
        bitLength /=2;

        BigInteger A = new BigInteger(bitLength - 1, random).nextProbablePrime();
        BigInteger B = new BigInteger(bitLength + 1, random).nextProbablePrime();
        BigInteger N = A.multiply(B);

        log("A", A);
        log("B", B);
        log("N", N);

        solve(N);
    }

    public void stress(int bitLength, long timeMillis){
        logsAvailable = false;
        for (int i = 0; i < 10; i++) {
            start(bitLength);
        }
        double count = 0;
        long startTime = System.nanoTime();
        while (System.nanoTime() - startTime < timeMillis * 1_000_000 ){
            count++;
            start(bitLength);
        }
        logsAvailable = true;

        double speed = count * 1_000_000_000L / (System.nanoTime() - startTime);
        log("count", count, "speed", speed);
    }

    protected abstract void solve(BigInteger n);

    protected void log(Object... objects) {
        if(!logsAvailable){
            return;
        }
        StringBuilder out = new StringBuilder();
        for (Object object : objects) {
            out.append(object).append(" ");
        }

        long milliseconds = (System.nanoTime() - startTime) / 1_000_000;

        String prefix = milliseconds + "> ";
        System.out.println(prefix + out);
    }
}
