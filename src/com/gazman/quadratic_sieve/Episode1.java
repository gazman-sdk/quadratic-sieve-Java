package com.gazman.quadratic_sieve;

import java.math.BigInteger;
import java.util.Random;

public class Episode1 extends BaseFact {

    public static void main(String[] args) {
        new Episode1().stress(30, 1000);
    }

    @Override
    protected void solve(BigInteger N) {
        Random random = new Random(123);

        while (true){
            BigInteger b = new BigInteger(N.bitLength() / 2 + random.nextInt(5), random).abs();
            BigInteger a = b.pow(2).subtract(N);
            if(isSquare(a)){
                log("found it", b.subtract(a.sqrt()));
                return;
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
