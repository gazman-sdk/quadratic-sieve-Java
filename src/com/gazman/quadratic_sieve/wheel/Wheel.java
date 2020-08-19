package com.gazman.quadratic_sieve.wheel;

import com.gazman.quadratic_sieve.core.siever.BSmoothData;

import java.util.List;

public class Wheel {
    private long currentPosition;
    private long startingPosition;
    private long prime;
    private int primeId;
    private double log;

    public void init(long prime, long startingPosition, int primeId) {
        this.currentPosition = startingPosition;
        this.startingPosition = startingPosition;
        this.prime = prime;
        this.primeId = primeId;
        log = Math.log(this.prime);
    }

    public void update(double[] logs) {
        for (; currentPosition < logs.length; currentPosition += prime) {
            logs[(int) currentPosition] += log;
        }

        this.currentPosition -= logs.length;
    }

    public void update(List<BSmoothData> bSmoothDataList) {
        for (BSmoothData bSmoothData : bSmoothDataList) {
            if ((bSmoothData.localX - startingPosition) % prime == 0) {
                bSmoothData.vector.set(primeId);
            }
        }
    }
}
