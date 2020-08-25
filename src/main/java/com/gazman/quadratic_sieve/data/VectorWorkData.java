package com.gazman.quadratic_sieve.data;

import java.math.BigInteger;

public class VectorWorkData {
    public final PolynomialData polynomialData;
    public final long localX;
    public final BigInteger c;

    public VectorWorkData(PolynomialData polynomialData, long localX, BigInteger c) {
        this.polynomialData = polynomialData;
        this.localX = localX;
        this.c = c;
    }
}
