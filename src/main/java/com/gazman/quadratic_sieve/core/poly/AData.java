package com.gazman.quadratic_sieve.core.poly;

import java.util.BitSet;
import java.util.List;

public class AData {
    public final List<Integer> primesIndexes;
    public final BitSet vector = new BitSet();

    public AData(List<Integer> primesIndexes) {
        this.primesIndexes = primesIndexes;

        for (int primeIndex : primesIndexes) {
            vector.set(primeIndex);
        }
    }
}
