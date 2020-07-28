package com.gazman.quadratic_sieve.matrix;

import com.gazman.quadratic_sieve.data.BSmooth;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

class GaussianEliminationMatrixTest {
    private static final BigInteger[] PRIMES = {
            BigInteger.valueOf(2),
            BigInteger.valueOf(3),
            BigInteger.valueOf(5),
            BigInteger.valueOf(7),
            BigInteger.valueOf(11),
            BigInteger.valueOf(13),
    };
    private List<BSmooth> bSmoothList;


    @BeforeEach
    void setUp() {
        BSmooth[] bSmooths = {
                bSmooth(8, 3, 1, 2, 2, 5),
                bSmooth(1, 2, 2, 1, 0, 3),
                bSmooth(3, 0, 4, 0, 3, 2),
                bSmooth(7, 7, 3, 2, 8, 4),
                bSmooth(0, 0, 5, 0, 0, 7),
                bSmooth(0, 4, 2, 3, 0, 8),
                bSmooth(0, 5, 4, 2, 2, 1),
                bSmooth(9, 2, 0, 1, 4, 0),
        };

        bSmoothList = Arrays.asList(bSmooths);
    }

    private BSmooth bSmooth(int... powers) {
        BitSet vector = new BitSet();
        BigInteger b = BigInteger.ONE;

        for (int i = 0; i < PRIMES.length; i++) {
            BigInteger prime = PRIMES[i];
            b = b.multiply(prime.pow(powers[i]));
            vector.set(i, powers[i] % 2 == 1);
        }


        return new BSmooth(BigInteger.ONE, b, vector);
    }

    @Test
    void solve() {

//        printVectors();

        List<List<BSmooth>> solutions = new GaussianEliminationMatrix().solve(bSmoothList, Arrays.asList(PRIMES));
        Assert.assertEquals(2, solutions.size());
        Assert.assertEquals(product(16, 16, 8, 8, 10, 20), product(solutions.get(0)));
        Assert.assertEquals(product(18, 12, 12, 6, 8, 16), product(solutions.get(1)));
    }

    private BigInteger product(List<BSmooth> bSmooths) {
        BigInteger b = BigInteger.ONE;
        for (BSmooth bSmooth : bSmooths) {
            b = b.multiply(bSmooth.b);
        }
        return b;
    }

    private BigInteger product(int... powers) {
        BigInteger b = BigInteger.ONE;
        for (int i = 0; i < powers.length; i++) {
            b = b.multiply(PRIMES[i].pow(powers[i]));
        }
        return b;
    }

    private void printVectors() {
        for (BSmooth bSmooth : bSmoothList) {
            for (BigInteger prime : PRIMES) {
                System.out.print(extractPower(bSmooth.b, prime));
                System.out.print(" ");
            }

            System.out.print("  ->  ");

            for (int i = 0; i < PRIMES.length; i++) {
                System.out.print(bSmooth.vector.get(i) ? 1 : 0);
                System.out.print(" ");
            }

            System.out.println();
        }
    }

    private int extractPower(BigInteger b, BigInteger prime) {
        int count = 0;
        while (b.mod(prime).equals(BigInteger.ZERO)) {
            count++;
            b = b.divide(prime);
        }
        return count;
    }


}