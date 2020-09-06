package com.gazman.quadratic_sieve.core.siever;

import java.util.Arrays;
import java.util.BitSet;

public class BSmoothData {
    public int localX;
    public BitSet vector;
    public final long[] valueA = {1, 1, 1};
    public final long[] valueB = {1, 1, 1};
    public long reminderA;
    public long reminderB;
    public int valueAIndex = 0;
    public int valueBIndex = 0;
    public boolean ignoreA;
    public boolean ignoreB;

    public BSmoothData(int localX) {
        this.localX = localX;
        vector = new BitSet();
    }

    public void init(int localX){
        this.localX = localX;
        vector = new BitSet();

        Arrays.fill(valueA, 1);
        Arrays.fill(valueB, 1);
        valueAIndex = valueBIndex = 0;
        reminderA = reminderB = 0;
        ignoreA = ignoreB = false;
    }
}
