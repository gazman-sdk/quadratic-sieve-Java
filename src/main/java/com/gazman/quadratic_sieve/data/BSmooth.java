package com.gazman.quadratic_sieve.data;

import java.math.BigInteger;
import java.util.BitSet;

public class BSmooth {
    private final long localX;
    private final PolynomialData polynomialData;
    public BigInteger a, b;
    public final BitSet originalVector;
    public BitSet vector;
    public boolean bigPrime;

    public BSmooth(PolynomialData polynomialData, long localX, BitSet vector) {
        this.vector = vector;
        this.polynomialData = polynomialData;
        this.localX = localX;
        originalVector = (BitSet) vector.clone();
    }

    public BigInteger getA() {
        return a != null ? a : polynomialData.getA(localX);
    }

    public BigInteger getB() {
        return b != null ? b : polynomialData.getB(localX);
    }

}
