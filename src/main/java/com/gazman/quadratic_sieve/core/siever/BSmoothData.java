package com.gazman.quadratic_sieve.core.siever;

import java.math.BigInteger;
import java.util.BitSet;

public class BSmoothData {
    public byte log;
    public final long localX;
    public long reminder;
    public final BitSet vector = new BitSet();

    public long value = 1;
    public BigInteger bigValue;

    public BSmoothData(long localX, BitSet baseVector) {
        this.localX = localX;
        vector.xor(baseVector);
    }
}
