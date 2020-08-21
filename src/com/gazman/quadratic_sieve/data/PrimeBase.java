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
    public final List<BigInteger> primeBaseBigInteger = new ArrayList<>();
    public final Map<BigInteger, Integer> primeBaseMap = new HashMap<>();
    public Integer maxPrime;
    public BigInteger maxPrimeBigInteger;
    public int minPrime;

    public void build(BigInteger N){
        List<Integer> primes = SieveOfEratosthenes.findPrimes(MagicNumbers.instance.primeBaseSize * 30);
        primeBase.add(-1);
        primeBaseBigInteger.add(BigInteger.valueOf(-1));
        for (int i = 0; primeBase.size() < MagicNumbers.instance.primeBaseSize; i++) {
            int prime = primes.get(i);
            BigInteger p = BigInteger.valueOf(prime);
            if (MathUtils.isRootInQuadraticResidues(N, p)) {
                primeBaseMap.put(p, primeBase.size());
                primeBase.add(prime);
                primeBaseBigInteger.add(BigInteger.valueOf(prime));
            }
        }

        int minPrime = 0;
        for (Integer prime : primes) {
            if(prime < MagicNumbers.instance.minPrimeSize){
                minPrime = prime;
            }
            else{
                break;
            }
        }

        this.minPrime = minPrime;

        log("Prime-base", Logger.formatLong(primeBase.size()));
        maxPrime = primeBase.get(primeBase.size() - 1);
        maxPrimeBigInteger = BigInteger.valueOf(maxPrime);
        log("Max-prime", Logger.formatLong(maxPrime));
    }
}
