package com.gazman.quadratic_sieve;

import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.utils.MathUtils;
import com.gazman.quadratic_sieve.utils.ModularSqrt;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.gazman.quadratic_sieve.debug.Logger.log;

public class JustForFun {

    public static void main(String[] args) {
        BigInteger n = QuadraticSieve.generateN(300, 123);
        BigInteger n2 = n.multiply(BigInteger.TWO);

        List<BigInteger> smallPrimeBase = buildSmallPrimeBase(n2, 10_000);


        BigInteger prime = BigDecimal.valueOf(Math.pow(n2.doubleValue(), 1 / 6.0) + 5).toBigInteger();

        ExecutorService executor = Executors.newWorkStealingPool();

        while (true) {
            prime = prime.nextProbablePrime();
            BigInteger finalPrime = prime;
            executor.execute(() -> {
                if (!MathUtils.isRootInQuadraticResidues(n2, finalPrime)) {
                    return;
                }

                BigInteger sqr = MathUtils.modSqrt(n2, finalPrime);
                BigInteger q = finalPrime.pow(2);
                BigInteger mod = n2.mod(q);

                BigInteger p1 = q.subtract(ModularSqrt.modularSqrtModPower(mod, q, finalPrime, sqr));
                BigInteger p2 = q.subtract(ModularSqrt.modularSqrtModPower(mod, q, finalPrime, finalPrime.subtract(sqr)));

                factor(n2, finalPrime, p1, smallPrimeBase);
                factor(n2, finalPrime, p2, smallPrimeBase);
            });
        }
    }

    private static int min = 1000;
    private static int minC = 1000;

    private static void factor(BigInteger n2, BigInteger f, BigInteger c, List<BigInteger> smallPrimeBase) {
        if (c.bitLength() > 100) {
            return;
        }
        if (c.bitLength() < minC) {
            log("MinC", format("c", c));
            minC = c.bitLength();
        }
        f = f.pow(2);
        BigInteger ab = n2.subtract(c.pow(2));
        BigInteger reminder = ab.divide(f);

        for (int i = 0, size = smallPrimeBase.size(); i < size; i++) {
            BigInteger p = smallPrimeBase.get(i);
            while (true) {
                BigInteger mod = reminder.mod(p);
                if (!mod.equals(BigInteger.ZERO)) {
                    break;
                }
                reminder = reminder.divide(p);
                f = f.multiply(p);
            }
        }
        if (reminder.bitLength() <= min) {
            min = reminder.bitLength();
            log("min", min, format("c", c), format("f", f.sqrt()), format("db", reminder));
        }
    }

    private static List<BigInteger> buildSmallPrimeBase(BigInteger n2, int primeBaseSize) {
        PrimeBase.instance.build(n2, primeBaseSize);
        List<BigInteger> smallPrimeBase = new ArrayList<>(PrimeBase.instance.primeBase.size());
        smallPrimeBase.add(BigInteger.valueOf(4));
        for (int prime : PrimeBase.instance.primeBase) {
            smallPrimeBase.add(BigInteger.valueOf(prime * (long) prime));
        }
        return smallPrimeBase;
    }

    private static String format(String name, BigInteger v) {
        return name + " " + String.format("%s(%d)", v, v.bitLength());
    }

}
