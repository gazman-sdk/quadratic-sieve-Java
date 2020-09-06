package com.gazman.quadratic_sieve.data;

import com.gazman.quadratic_sieve.wheel.Wheel;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class PolynomialData {

    public final BigInteger N;
    public final BigInteger a;
    public final BigInteger c;
    public final BigInteger x;
    public final BigInteger iStart;
    public final double scale;

    private final BigInteger xMinusN;
    private final BigInteger cPlusIStart;

    public PolynomialData(BigInteger N, BigInteger a, BigInteger c, BigInteger x, BigInteger iStart, double scale) {
        this.N = N;
        this.a = a;
        this.c = c;
        this.x = x;
        this.iStart = iStart;
        this.scale = scale;

        xMinusN = x.subtract(N);
        cPlusIStart = c.add(iStart);
    }

    /**
     * ia + x`- N
     */
    public BigInteger getSievingValueA(int localX) {
        return iStart.add(BigInteger.valueOf(localX)).multiply(a).add(xMinusN);
    }

    /**
     * c + i
     */
    public BigInteger getSievingValueB(int localX) {
        return cPlusIStart.add(BigInteger.valueOf(localX));
    }

    public List<Wheel> buildWheels() {
        List<Wheel> wheels = new ArrayList<>(PrimeBase.instance.primeBase.size());

        List<Integer> primeBase = PrimeBase.instance.primeBase;

        BigInteger aStart = getSievingValueA(0);
        BigInteger bStart = getSievingValueB(0);

        for (int i = 0, primeBaseSize = primeBase.size(); i < primeBaseSize; i++) {
            int prime = primeBase.get(i);
            BigInteger p = BigInteger.valueOf(prime);

            int a = p.subtract(aStart.mod(p)).intValue();
            int b = p.subtract(bStart.mod(p)).intValue();

            wheels.add(new Wheel(a,b, prime,i, scale));
        }
        return wheels;
    }


    public BigInteger getA(int localX) {
        return x.add(a.multiply(BigInteger.valueOf(localX)));
    }

    public BigInteger getB(int localX) {
        return getSievingValueA(localX).multiply(getSievingValueB(localX)).multiply(a);
    }
}
