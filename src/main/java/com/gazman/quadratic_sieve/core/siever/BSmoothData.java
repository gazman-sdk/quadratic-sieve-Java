package com.gazman.quadratic_sieve.core.siever;

import java.util.BitSet;

public class BSmoothData {
    public final long localX;
    public long reminder;
    public final BitSet vector = new BitSet();

    public long[] value = {1,1,1};
    public int valueIndex = 0;

    public BSmoothData(long localX, BitSet baseVector) {
        this.localX = localX;
        vector.xor(baseVector);
    }
}
