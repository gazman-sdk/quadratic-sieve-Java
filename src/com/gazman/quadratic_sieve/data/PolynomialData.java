package com.gazman.quadratic_sieve.data;

import com.gazman.quadratic_sieve.wheel.Wheel;

import java.math.BigInteger;
import java.util.List;

public class PolynomialData {
    public final BigInteger a;
    public final BigInteger b;
    public final BigInteger N;
    public final List<Wheel> wheels;


    public PolynomialData(BigInteger a, BigInteger b, BigInteger N, List<Wheel> wheels) {
        this.a = a;
        this.b = b;
        this.N = N;
        this.wheels = wheels;
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
