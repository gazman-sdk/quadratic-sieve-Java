package com.gazman.quadratic_sieve.core.poly;

import java.util.BitSet;
import java.util.List;

public class AData {
    public final List<Integer> primesIndexes;
    public final BitSet vector;

    public AData(List<Integer> primesIndexes) {
        this.primesIndexes = primesIndexes;
        vector = new BitSet(primesIndexes.get(primesIndexes.size() - 1));

        for (int primeIndex : primesIndexes) {
            vector.set(primeIndex);
        }
    }
}
