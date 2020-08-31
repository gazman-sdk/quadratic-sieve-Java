package com.gazman.quadratic_sieve;

import com.gazman.quadratic_sieve.core.matrix.Matrix;
import com.gazman.quadratic_sieve.core.poly.PolyMiner;
import com.gazman.quadratic_sieve.core.siever.Siever;
import com.gazman.quadratic_sieve.data.MagicNumbers;
import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.debug.Logger;

import java.math.BigInteger;
import java.util.Random;

import static com.gazman.quadratic_sieve.debug.Logger.log;

public class QuadraticSieve{

    private static long startTime;

    public static void main(String[] args) {
        solve(generateN(MagicNumbers.DEMO_BIT_SIZE));
    }

    private static void solve(BigInteger N) {
        startTime = System.nanoTime();
        MagicNumbers.instance.initN(N);
        PrimeBase.instance.build(N);
        MagicNumbers.instance.init();

        PolyMiner.instance.start(N);
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        for (int i = 0; i < availableProcessors; i++) {
            //noinspection ObjectAllocationInLoop
            new Siever().start();
        }
        Matrix.instance.start(N);
    }

    public static void shutDown(BigInteger solution) {
        System.out.println("Oh yeah " + solution + "\nCompleted in " + Logger.formatLong((System.nanoTime() - startTime) / 1_000_000));
        System.exit(0);
    }

    @SuppressWarnings("ObjectAllocationInLoop")
    public static BigInteger generateN(int bitLength) {
        bitLength /= 2;

        long seed;
        //noinspection ConstantConditions
        if (MagicNumbers.DEMO_SEED == -1) {
            Random random = new Random();
            seed = random.nextLong();
        } else {
            seed = MagicNumbers.DEMO_SEED;
        }

        Random random = new Random(seed);

        BigInteger N, A, B;
        N = A = B = BigInteger.ONE;
        //noinspection MethodCallInLoopCondition
        while (N.bitLength() != bitLength << 1) {
            A = new BigInteger(bitLength - 1, random).nextProbablePrime();
            B = new BigInteger(bitLength + 1, random).nextProbablePrime();
            N = A.multiply(B);
        }

        log("Seed", seed);
        log("A", A);
        log("B", B);
        log("N", N + "(" + N.bitLength() + ")");
        return N;
    }
}