package com.gazman.quadratic_sieve;

import java.math.BigInteger;
import java.util.Random;

public class Episode1 extends BaseFact {

    public static void main(String[] args) {
        new Episode1().start(44);
    }

    @Override
    protected void solve(BigInteger N) {
        Random random = new Random(123);

        for (int i = 0; i < 10; ) {
            BigInteger b = new BigInteger(N.bitLength() / 2 + 1, random).abs();
            BigInteger a = b.pow(2).subtract(N);
            if(isSquare(a)){
                log("found it", b.subtract(a.sqrt()));
                i++;
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
