package com.gazman.quadratic_sieve.core.siever;

import java.math.BigInteger;
import java.util.BitSet;

public class BSmoothData {
    public byte log;
    public long localX;
    public long reminder;
    public BitSet vector = new BitSet();

    public long value = 1;
    public BigInteger bigValue;

    public void reset() {
        log = 0;
        localX = 0;
        reminder = 0;
        vector = new BitSet();
        bigValue = null;
        value = 1;
    }
}
