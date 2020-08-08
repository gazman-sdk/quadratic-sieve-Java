package com.gazman.quadratic_sieve.data;

import java.math.BigInteger;
import java.util.BitSet;

public class BSmooth {
    private BigInteger localX;
    private PolynomialData polynomialData;
    public BigInteger a, b;
    public BitSet vector;

    public BSmooth(BigInteger a, BigInteger b, BitSet vector) {
        this.a = a;
        this.b = b;
        this.vector = vector;
    }

    public BSmooth(PolynomialData polynomialData, BigInteger localX, BitSet vector) {
        this.vector = vector;
        this.polynomialData = polynomialData;
        this.localX = localX;
    }

    public BigInteger getA() {
        return a != null ? a : polynomialData.getA(localX);
    }

    public BigInteger getB() {
        return b != null ? b : polynomialData.getB(localX);
    }

    public BSmooth copy() {
        return new BSmooth(a,b, (BitSet) vector.clone());
    }
}
