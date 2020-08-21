package com.gazman.quadratic_sieve.core;

import com.gazman.quadratic_sieve.logger.Logger;

import java.math.BigInteger;
import java.util.Random;

public abstract class BaseFact {
    private final Random random = new Random(1223);

    protected void start(int bitLength) {
        solve(generateN(bitLength));
    }

    public BigInteger generateN(int bitLength) {
        bitLength /=2;

        BigInteger A = new BigInteger(bitLength - 1, random).nextProbablePrime();
        BigInteger B = new BigInteger(bitLength + 1, random).nextProbablePrime();
        BigInteger N = A.multiply(B);

        log("A", A);
        log("B", B);
        log("N", N);
        return N;
    }

    public void stress(int bitLength, long timeMillis){
        Logger.setLogsAvailable(false);
        for (int i = 0; i < 10; i++) {
            start(bitLength);
        }
        double count = 0;
        long startTime = System.nanoTime();
        while (System.nanoTime() - startTime < timeMillis * 1_000_000 ){
            count++;
            start(bitLength);
        }
        Logger.setLogsAvailable(true);

        double speed = count * 1_000_000_000L / (System.nanoTime() - startTime);
        log("count", count, "speed", speed);
    }

    protected abstract void solve(BigInteger n);

    protected void log(Object... objects) {
        Logger.log(objects);
    }
}
