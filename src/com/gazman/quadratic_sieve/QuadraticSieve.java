package com.gazman.quadratic_sieve;

import com.gazman.quadratic_sieve.core.*;
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
        MagicNumbers.instance.init(N);
        PrimeBase.instance.build(N);
        MagicNumbers.instance.initMaxPrimeThreshold(PrimeBase.instance.maxPrime);
        PolyMiner.instance.start(N);
        new Siever().start();
        new Siever().start();
        Matrix.instance.start(N);
    }
}