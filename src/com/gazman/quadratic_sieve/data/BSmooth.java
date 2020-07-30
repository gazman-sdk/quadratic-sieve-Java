package com.gazman.quadratic_sieve.data;

import java.math.BigInteger;
import java.util.BitSet;

public class BSmooth {
    public final BigInteger a, b;
    public BitSet vector;

    public BSmooth(BigInteger a, BigInteger b, BitSet vector) {
        this.a = a;
        this.b = b;
        this.vector = vector;
    }

    public BSmooth copy() {
        return new BSmooth(a,b, (BitSet) vector.clone());
    }
}
