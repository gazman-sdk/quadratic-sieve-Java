package com.gazman.quadratic_sieve;

import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.utils.MathUtils;
import com.gazman.quadratic_sieve.utils.ModularSqrt;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gazman.quadratic_sieve.debug.Logger.log;

public class JustForFun {

    public static void main(String[] args) {
        BigInteger n = QuadraticSieve.generateN(300, 123);
        BigInteger n2 = n.multiply(BigInteger.TWO);

        PrimeBase.instance.build(n2, 100_000);
        log();

        List<Integer> primes = PrimeBase.instance.primeBase;

        int size = primes.size();
        long[][] positions = new long[size][2];

        int firstPrime = 1000;
        int intLimit = (int) Math.sqrt(Integer.MAX_VALUE);

        for (int i = firstPrime; i < size; i++) {
            Integer p = primes.get(i);
            BigInteger prime = BigInteger.valueOf(p);
            if (prime.equals(BigInteger.TWO)) {
                continue;
            }

            long[] flats = MathUtils.ressol(prime.longValue(), n2.mod(prime).longValue());
            if (p < intLimit) {

                int q = p * p;

                BigInteger mod = n2.mod(BigInteger.valueOf(q));

                flats[0] = q - ModularSqrt.modularSqrtModPower(mod, q, p, (int) flats[0]);
                flats[1] = q - ModularSqrt.modularSqrtModPower(mod, q, p, (int) flats[1]);

            } else {
                BigInteger q = prime.pow(2);

                BigInteger mod = n2.mod(q);

                flats[0] = q.subtract(ModularSqrt.modularSqrtModPower(mod, q, prime, BigInteger.valueOf(flats[0]))).longValue();
                flats[1] = q.subtract(ModularSqrt.modularSqrtModPower(mod, q, prime, BigInteger.valueOf(flats[1]))).longValue();

            }
            Arrays.sort(flats);
            positions[i] = flats;
        }

        log("Calculated positions");
        log();

        Map<Long, BigInteger> map = new HashMap<>(1024 * 10);


        int min = 10000;
        long step = 10_000_000_000L;
        long target = 0;

        for (int k = 0; k < 1000; k++) {
            target += step;
            map.clear();


            for (int i = firstPrime; i < size; i++) {
                long q = primes.get(i) * (long) primes.get(i);
                BigInteger p = BigInteger.valueOf(q);

                long[] position = positions[i];
                for (int j = 0; j < 2; j++) {
                    while (position[j] < target) {
                        map.put(position[j], map.getOrDefault(position[j], BigInteger.ONE).multiply(p));
                        position[j] += q;
                    }
                }
            }

            for (Map.Entry<Long, BigInteger> entry : map.entrySet()) {
                BigInteger a = entry.getValue();
                if (a.bitLength() < 50) {
                    continue;
                }

                BigInteger ab = n2.subtract(BigInteger.valueOf(entry.getKey()).pow(2));

                BigInteger reminder = ab.divide(a);

                for (int i = 0; i < firstPrime; i++) {
                    BigInteger p = BigInteger.valueOf(primes.get(i)).pow(2);
                    while (true) {
                        BigInteger mod = reminder.mod(p);
                        if (!mod.equals(BigInteger.ZERO)) break;
                        reminder = reminder.divide(p);
                        a = a.multiply(p);
                    }
                }
                if (reminder.bitLength() <= min) {
                    min = reminder.bitLength();
                    log("min", min, "2n",n2, "c", entry.getKey(), "f", a.sqrt(), "db", reminder);
                }
            }
        }
        log("Sieve done Map size", map.size());
        log();
    }
}
