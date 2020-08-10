package com.gazman.quadratic_sieve.data;

import java.math.BigInteger;

public class VectorWorkData {
    public final PolynomialData polynomialData;
    public final BigInteger localX;
    public final BigInteger c;

    public VectorWorkData(PolynomialData polynomialData, BigInteger localX, BigInteger c) {
        this.polynomialData = polynomialData;
        this.localX = localX;
        this.c = c;
    }
}
