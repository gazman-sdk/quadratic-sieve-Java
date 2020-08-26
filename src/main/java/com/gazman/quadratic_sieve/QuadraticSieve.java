package com.gazman.quadratic_sieve;

import com.gazman.quadratic_sieve.core.BaseFact;
import com.gazman.quadratic_sieve.core.matrix.Matrix;
import com.gazman.quadratic_sieve.core.poly.PolyMiner;
import com.gazman.quadratic_sieve.core.siever.Siever;
import com.gazman.quadratic_sieve.data.MagicNumbers;
import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.debug.Logger;

import java.math.BigInteger;

public class QuadraticSieve extends BaseFact {

    private static long startTime;

    public static void main(String[] args) {
        startTime = System.nanoTime();
        new QuadraticSieve().start(200);
    }

    public static void shutDown(BigInteger solution){
        System.out.println("Oh yeah " + solution + "\nCompleted in " + Logger.formatLong((System.nanoTime() - startTime) / 1_000_000));
        System.exit(0);
    }

    @Override
    protected void solve(BigInteger N) {
        MagicNumbers.instance.initN(N);
        PrimeBase.instance.build(N);
        MagicNumbers.instance.init();

        PolyMiner.instance.start(N);
        for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
            new Siever().start();
        }
        Matrix.instance.start(N);
    }
}