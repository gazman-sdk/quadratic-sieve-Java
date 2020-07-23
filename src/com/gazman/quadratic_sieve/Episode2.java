package com.gazman.quadratic_sieve;

import java.math.BigInteger;
import java.util.Random;

public class Episode2 extends BaseFact {

    public static void main(String[] args) {
        new Episode2().start(70);
    }

    @Override
    protected void solve(BigInteger N) {

        BigInteger sqrt = N.sqrt();
        for (int i = 1, k = 1; ; i++) {
            if(i % 100 == 0){
                k++;
                sqrt = N.multiply(BigInteger.valueOf(k)).sqrt();
                i = 0;
            }
            BigInteger v = BigInteger.valueOf(i);
            BigInteger a = sqrt.add(v);
            BigInteger mod = a.pow(2).mod(N);
            if(isSquare(mod)){
                BigInteger maybeASolution = a.add(mod.sqrt()).gcd(N);
                if(!maybeASolution.equals(N) && !maybeASolution.equals(BigInteger.ONE)){
                    log(k, "Oh yeah", maybeASolution);
                    break;
                }
            }
        }
    }

    private boolean isSquare(BigInteger a) {
        if(a.compareTo(BigInteger.ZERO) < 0){
            return false;
        }
        return a.sqrt().pow(2).equals(a);
    }
}
