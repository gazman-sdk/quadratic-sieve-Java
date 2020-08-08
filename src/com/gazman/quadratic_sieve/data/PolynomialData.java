package com.gazman.quadratic_sieve.data;

import java.math.BigInteger;

public class PolynomialData {
    public final BigInteger a,b,N;


    public PolynomialData(BigInteger a, BigInteger b, BigInteger N) {
        this.a = a;
        this.b = b;
        this.N = N;
    }


    public BigInteger getC(){
        BigInteger ac = b.pow(2).subtract(N);
        return ac.divide(a);
    }

    public BigInteger getSievingValue(BigInteger position, BigInteger c){
        return a.multiply(position.pow(2)).add(b.multiply(position).multiply(BigInteger.TWO).add(c));
    }

    public BigInteger getA(BigInteger localX){
        return a.multiply(localX).add(b);
    }

    public BigInteger getB(BigInteger localX){
        return a.multiply(localX).add(b).pow(2).subtract(N);
    }

}
