package com.gazman.quadratic_sieve.wheel;

import com.gazman.quadratic_sieve.core.siever.BSmoothData;
import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.utils.ByteArray;

import java.util.List;

public class Wheel {
    private int currentPosition;
    private int startingPosition;
    public final int prime;
    private final int primeIndex;
    private int delta;
    private final byte log;
    private final long maxLong = Long.MAX_VALUE / PrimeBase.instance.maxPrime;

    public Wheel(int prime, int primeIndex, int startingPosition, int delta, double scale) {
        this.prime = prime;
        this.primeIndex = primeIndex;
        reset(startingPosition, delta);
        log = (byte) Math.round(Math.log(prime) * scale);
    }

    public void reset(int startingPosition, int delta) {
        this.delta = delta;
        this.startingPosition = (startingPosition + prime - delta % prime) % prime;
        this.currentPosition = this.startingPosition;
    }

    public void update(ByteArray logs) {
        int length = logs.capacity;
        int currentPosition = this.currentPosition;
        byte log = this.log;
        int prime = this.prime;
        while (currentPosition < length) {
            logs.add(currentPosition, log);
            currentPosition += prime;
        }

        this.currentPosition = currentPosition - length;
    }

    public void updateSmooth(List<BSmoothData> bSmoothList) {
        for (int i = 0, bSmoothListSize = bSmoothList.size(); i < bSmoothListSize; i++) {
            BSmoothData bSmoothData = bSmoothList.get(i);
            if ((bSmoothData.localX - delta) % prime == startingPosition) {
                bSmoothData.vector.flip(primeIndex);

                if (bSmoothData.value[bSmoothData.valueIndex] > maxLong) {
                    bSmoothData.valueIndex++;
                }
                bSmoothData.value[bSmoothData.valueIndex] *= prime;
            }
        }
    }
}
