package com.gazman.quadratic_sieve.data;

import com.gazman.quadratic_sieve.wheel.Wheel;

import java.math.BigInteger;
import java.util.List;

public class PolynomialData {
    public final BigInteger a;
    public final BigInteger b;
    public final BigInteger N;
    public final List<Wheel> wheels;
    public final BigInteger c;


    public PolynomialData(BigInteger a, BigInteger b, BigInteger N, List<Wheel> wheels) {
        this.a = a;
        this.b = b;
        this.N = N;
        this.wheels = wheels;
        c = b.pow(2).subtract(N).divide(a);
    }

    public int getLoopsSize() {
        return b.pow(2).subtract(a.multiply(c)).sqrt().add(b).divide(a).multiply(BigInteger.TWO).intValue() /
                MagicNumbers.instance.loopsCount;
    }

    public BigInteger getSievingValue(long localX) {
        BigInteger x = BigInteger.valueOf(localX);
        return a.multiply(x.pow(2)).add(b.multiply(x).multiply(BigInteger.TWO).add(c));
    }

    public BigInteger getA(long localX) {
        return a.multiply(BigInteger.valueOf(localX)).add(b);
    }

    public BigInteger getB(long localX) {
        return getA(localX).pow(2).subtract(N);
    }

}
