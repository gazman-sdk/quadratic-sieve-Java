package com.gazman.quadratic_sieve.primes;

import java.util.*;

public class SieveOfEratosthenes {

    public static List<Integer> findPrimes(int limit) {
        if (limit < 3) {
            return Collections.emptyList();
        }

        switch (limit) {
            case 3:
                return Collections.singletonList(2);
            case 4:
            case 5:
                return Arrays.asList(2, 3);
            case 6:
            case 7:
                return Arrays.asList(2, 3, 5);
            case 8:
                return Arrays.asList(2, 3, 5, 7);
        }

        boolean[] booleans = new boolean[limit / 2 + 1];
        int size = (int) Math.sqrt(limit);
        size += size % 2;
        List<Integer> primes = new ArrayList<>();
        primes.add(2);
        for (int prime = 3; prime < size; prime += 2) {
            if (booleans[prime / 2 + 1]) {
                continue;
            }
            primes.add(prime);
            for (int i = prime / 2 + 1; i < booleans.length; i += prime) {
                booleans[i] = true;
            }
        }
        for (int i = size / 2; i < booleans.length; i++) {
            if (!booleans[i]) {
                int p = i * 2 - 1;
                primes.add(p);
            }
        }




        return primes;
    }
}
