package com.gazman.quadratic_sieve.data;

import java.math.BigInteger;
import java.util.BitSet;

public class VectorData {
    public final BitSet vector;
    public final BigInteger bigPrime;

    public VectorData(BitSet vector, BigInteger bigPrime) {
        this.vector = vector;
        this.bigPrime = bigPrime;
    }
}
