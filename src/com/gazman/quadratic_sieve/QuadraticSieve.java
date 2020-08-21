package com.gazman.quadratic_sieve;

import com.gazman.quadratic_sieve.core.BaseFact;
import com.gazman.quadratic_sieve.core.matrix.Matrix;
import com.gazman.quadratic_sieve.core.poly.PolyMiner;
import com.gazman.quadratic_sieve.core.siever.Siever;
import com.gazman.quadratic_sieve.data.MagicNumbers;
import com.gazman.quadratic_sieve.data.PrimeBase;

import java.math.BigInteger;

public class QuadraticSieve extends BaseFact {

    public static void main(String[] args) {
        new QuadraticSieve().start(200);
    }

    @Override
    protected void solve(BigInteger N) {
        MagicNumbers.instance.initN(N);
        PrimeBase.instance.build(N);
        MagicNumbers.instance.init();

        PolyMiner.instance.start(N);
        for (int i = 0; i < Runtime.getRuntime().availableProcessors() * 2; i++) {
            new Siever().start();
        }
        Matrix.instance.start(N);
    }
}