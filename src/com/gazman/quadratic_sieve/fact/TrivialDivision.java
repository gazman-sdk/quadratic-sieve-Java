package com.gazman.quadratic_sieve.fact;

import com.gazman.quadratic_sieve.BaseFact;

import java.math.BigInteger;

public class TrivialDivision extends BaseFact {

    public static void main(String[] args) {
        new TrivialDivision().stress(30, 1000);
    }

    @Override
    protected void solve(BigInteger N) {
        BigInteger sqrt = N.sqrt();
        for (int i = 0; ; i++) {
            BigInteger v = sqrt.add(BigInteger.valueOf(i));
            if(N.mod(v).equals(BigInteger.ZERO)){
                log("Got it", v);
                break;
            }
        }
    }
}
