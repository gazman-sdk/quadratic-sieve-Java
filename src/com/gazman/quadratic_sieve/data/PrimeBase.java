package com.gazman.quadratic_sieve.data;

import com.gazman.quadratic_sieve.logger.Logger;
import com.gazman.quadratic_sieve.primes.SieveOfEratosthenes;
import com.gazman.quadratic_sieve.utils.MathUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.gazman.quadratic_sieve.logger.Logger.log;

public class PrimeBase {

    public static final PrimeBase instance = new PrimeBase();

    public final List<Integer> primeBase = new ArrayList<>();
    public final Map<BigInteger, Integer> primeBaseMap = new HashMap<>();
    public Integer maxPrime;
    public BigInteger maxPrimeBigInteger;

    public void build(BigInteger N){
        List<Integer> primes = SieveOfEratosthenes.findPrimes(MagicNumbers.instance.B);
        for (int prime : primes) {
            BigInteger p = BigInteger.valueOf(prime);
            if (MathUtils.isRootInQuadraticResidues(N, p)) {
                primeBaseMap.put(p, primeBase.size());
                primeBase.add(prime);
            }
        }

        log("Prime-base", Logger.formatLong(primeBase.size()));
        maxPrime = primeBase.get(primeBase.size() - 1);
        maxPrimeBigInteger = BigInteger.valueOf(maxPrime);
        log("Max-prime", Logger.formatLong(maxPrime));
    }
}
