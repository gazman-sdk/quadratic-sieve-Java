package com.gazman.quadratic_sieve;

import java.math.BigInteger;

public class Episode2 extends BaseFact {

    public static void main(String[] args) {
        new Episode2().start(60);
    }

    @Override
    protected void solve(BigInteger N) {


        for (int i = 0; ; i++) {
            BigInteger a = N.sqrt().add(BigInteger.valueOf(i));
            BigInteger mod = a.pow(2).mod(N);

            if (isSquare(mod)) {
                BigInteger maybeSolution = a.add(mod.sqrt()).gcd(N);
                if (!maybeSolution.equals(N) && !maybeSolution.equals(BigInteger.ONE)) {
                    log("Oh yeah", maybeSolution);
                    break;
                }
            }
        }
    }

    private boolean isSquare(BigInteger a) {
        if (a.compareTo(BigInteger.ZERO) < 0) {
            return false;
        }
        return a.sqrt().pow(2).equals(a);
    }
}
