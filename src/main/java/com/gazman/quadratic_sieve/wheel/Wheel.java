package com.gazman.quadratic_sieve.wheel;

import com.gazman.quadratic_sieve.core.siever.BSmoothData;
import com.gazman.quadratic_sieve.data.MagicNumbers;
import com.gazman.quadratic_sieve.debug.AssertUtils;

import java.math.BigInteger;
import java.util.List;

public class Wheel {
    private int currentPosition;
    private int startingPosition;
    private final int prime;
    private final int primeIndex;
    private final int delta;
    private final byte log;
    private static byte longMaxLog = -1;

    public Wheel(int prime, int primeIndex, int startingPosition, int delta, double scale) {
        this.prime = prime;
        this.primeIndex = primeIndex;
        this.delta = delta;
        reset(startingPosition, delta);
        log = (byte) Math.round(Math.log(prime) * scale);
        if (longMaxLog == -1) {
            longMaxLog = (byte) Math.round(Math.log(Long.MAX_VALUE / MagicNumbers.instance.longRoundingError) * scale);
        }
    }

    public void reset(int startingPosition, int delta) {
        this.startingPosition = (startingPosition + prime - delta % prime) % prime;
        this.currentPosition = this.startingPosition;
    }

    public void update(byte[] logs) {
        for (; currentPosition < logs.length; currentPosition += prime) {
            logs[currentPosition] += log;
        }

        this.currentPosition -= logs.length;
    }

    public void updateSmooth(List<BSmoothData> bSmoothList) {
        for (BSmoothData bSmoothData : bSmoothList) {
            if ((bSmoothData.localX - delta) % prime == startingPosition) {
                bSmoothData.vector.set(primeIndex);
                bSmoothData.log += log;
                if (bSmoothData.bigValue == null) {
                    if (bSmoothData.log < longMaxLog) {
                        bSmoothData.value *= prime;
                        AssertUtils.assertTrue("Long overflow", ()-> bSmoothData.value > 0);
                    } else {
                        bSmoothData.bigValue = BigInteger.valueOf(bSmoothData.value)
                                .multiply(BigInteger.valueOf(prime));
                    }
                } else {
                    bSmoothData.bigValue = bSmoothData.bigValue.multiply(BigInteger.valueOf(prime));
                }
            }
        }
    }
}
