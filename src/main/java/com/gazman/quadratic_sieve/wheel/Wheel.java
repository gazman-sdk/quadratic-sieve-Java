package com.gazman.quadratic_sieve.wheel;

import com.gazman.quadratic_sieve.core.siever.BSmoothData;
import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.utils.ByteArray;

import java.util.List;

public class Wheel {

    private final byte log;
    private final int prime;
    private final int aPrimeIndex;
    private final int bPrimeIndex;
    private int currentAPosition; // Comes from getSievingValueA
    private int currentBPosition; // Comes from getSievingValueB
    private int startingAPosition;
    private int startingBPosition;
    private final long maxLong = Long.MAX_VALUE / PrimeBase.instance.maxPrime;

    public Wheel(int a, int b, int prime, int primeIndex, double scale) {
        this.prime = prime;
        this.aPrimeIndex = primeIndex + PrimeBase.instance.primeBase.size();
        this.bPrimeIndex = primeIndex;
        startingAPosition = currentAPosition = a;
        startingBPosition = currentBPosition = b;

        log = (byte) (Math.log(prime) * scale);
    }

    public void update(ByteArray logs) {
        int length = logs.capacity;
        byte log = this.log;
        int prime = this.prime;
        while (currentAPosition < length) {
            logs.add(currentAPosition, log);
            currentAPosition += prime;
        }
        while (currentBPosition < length) {
            logs.add(currentBPosition, log);
            currentBPosition += prime;
        }

        this.currentAPosition = currentAPosition - length;
        this.currentBPosition = currentBPosition - length;
    }

    public void updateSmooth(List<BSmoothData> bSmoothList) {
        for (int i = 0, bSmoothListSize = bSmoothList.size(); i < bSmoothListSize; i++) {
            BSmoothData bSmoothData = bSmoothList.get(i);
            int mod = bSmoothData.localX % prime;
            if (mod == startingAPosition) {
                bSmoothData.vector.flip(aPrimeIndex);

                if (bSmoothData.valueA[bSmoothData.valueAIndex] > maxLong) {
                    bSmoothData.valueAIndex++;
                }
                bSmoothData.valueA[bSmoothData.valueAIndex] *= prime;
            }
            else if (mod == startingBPosition) {
                bSmoothData.vector.flip(bPrimeIndex);

                if (bSmoothData.valueB[bSmoothData.valueBIndex] > maxLong) {
                    bSmoothData.valueBIndex++;
                }
                bSmoothData.valueB[bSmoothData.valueBIndex] *= prime;
            }
        }
    }
}
