package com.gazman.quadratic_sieve.core.siever;

import java.util.Arrays;
import java.util.BitSet;

public class BSmoothData {
    public int localX;
    public BitSet vector;
    public final long[] value = {1, 1, 1};
    public long reminder;
    public int valueIndex = 0;
    public boolean ignore;

    public BSmoothData(int localX, BitSet baseVector) {
        this.localX = localX;
        vector = new BitSet(baseVector.size());
        vector.xor(baseVector);
    }

    public void init(int localX, BitSet baseVector){
        this.localX = localX;
        vector = new BitSet(baseVector.size());
        vector.xor(baseVector);
        Arrays.fill(value, 1);
        valueIndex = 0;
        ignore = false;
        reminder = 0;
    }
}
