package com.gazman.quadratic_sieve.core.siever;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class BSmoothDataPool {
    private final Queue<BSmoothData> bSmoothData = new ConcurrentLinkedDeque<>();

    public static final BSmoothDataPool instance = new BSmoothDataPool();

    public BSmoothData get(long localX, double log) {
        BSmoothData bSmoothData = this.bSmoothData.poll();
        if (bSmoothData == null) {
            bSmoothData = new BSmoothData();
        }
        bSmoothData.localX = localX;
        bSmoothData.log = log;
        return bSmoothData;
    }

    public void put(BSmoothData bSmoothData) {
        this.bSmoothData.add(bSmoothData);
    }
}
