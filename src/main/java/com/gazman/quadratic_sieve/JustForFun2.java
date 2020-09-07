package com.gazman.quadratic_sieve;

import com.gazman.quadratic_sieve.utils.MathUtils;
import com.gazman.quadratic_sieve.utils.ModularSqrt;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static com.gazman.quadratic_sieve.debug.Logger.log;

public class JustForFun2 {

    public static void main(String[] args) {
        BigInteger n2 = QuadraticSieve.generateN(300, 123)
                .multiply(BigInteger.TWO);

        List<BigInteger> primeBase = buildMegaPrimes(BigInteger.TWO.pow(n2.bitLength() / 3), 20, n2);
        BigInteger[][] positions = findPowerPositions(primeBase, n2);

        BigInteger[] num = {primeBase.get(0), primeBase.get(1)};
        BigInteger[] rem = {positions[0][0], positions[1][0]};
        BigInteger c = MathUtils.chinesReminderBigInt(num, rem);

        log(n2.subtract(c.pow(2)).mod(primeBase.get(0)));
        log(n2.subtract(c.pow(2)).mod(primeBase.get(1)));
        log("c", c, "f", primeBase.get(0).multiply(primeBase.get(1)).sqrt());
    }

    private static BigInteger[][] findPowerPositions(List<BigInteger> primeBase, BigInteger n2) {

        BigInteger[][] positions = new BigInteger[20][2];
        for (int i = 0; i < primeBase.size(); i++) {
            BigInteger prime = primeBase.get(i);
            BigInteger q = prime.pow(2);


            BigInteger p1 = MathUtils.modSqrt(n2, prime);
            positions[i][0] = ModularSqrt.modularSqrtModPower(n2.mod(q), q, prime, p1);
            positions[i][1] = q.subtract(positions[i][0]);
        }

        return positions;
    }

    private static List<BigInteger> buildMegaPrimes(BigInteger prime, int count, BigInteger n2) {
        List<BigInteger> primes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            prime = prime.nextProbablePrime();
            if(MathUtils.isRootInQuadraticResidues(n2, prime)){
                primes.add(prime);
            }
            else {
                i--;
            }
        }
        return primes;
    }
}
