package com.gazman.quadratic_sieve.data;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.List;

public class BSmooth {
    public List<BSmooth> solution;
    private BigInteger localX;
    private PolynomialData polynomialData;
    public BigInteger a, b;
    public final BitSet originalVector;
    public BitSet vector;

    public BSmooth(BigInteger a, BigInteger b, BitSet vector) {
        this.a = a;
        this.b = b;
        this.vector = vector;
        this.originalVector = (BitSet) vector.clone();
    }

    public BSmooth(PolynomialData polynomialData, BigInteger localX, BitSet vector) {
        this.vector = vector;
        this.polynomialData = polynomialData;
        this.localX = localX;
        this.originalVector = (BitSet) vector.clone();
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
