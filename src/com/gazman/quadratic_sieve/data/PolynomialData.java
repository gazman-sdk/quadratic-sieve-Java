package com.gazman.quadratic_sieve.data;

import com.gazman.quadratic_sieve.wheel.Wheel;

import java.math.BigInteger;
import java.util.List;

public class PolynomialData {
    public final BigInteger a;
    public final BigInteger b;
    public final BigInteger c;
    public final BigInteger N;
    public List<Wheel> wheels;
    public final int delta;
    public double scale;


    public PolynomialData(BigInteger a, BigInteger b, BigInteger c, int delta, BigInteger N) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.N = N;
        this.delta = delta;
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
