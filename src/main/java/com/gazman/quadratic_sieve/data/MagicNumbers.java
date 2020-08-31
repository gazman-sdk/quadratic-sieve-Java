package com.gazman.quadratic_sieve.data;

import com.gazman.quadratic_sieve.debug.Logger;

import java.math.BigInteger;
import java.util.List;

import static com.gazman.quadratic_sieve.debug.Logger.log;

public class MagicNumbers {

    public static final MagicNumbers instance = new MagicNumbers();
    public static final int DEMO_BIT_SIZE = 220;
    public static final long DEMO_SEED = 123;
    public int minPrimeSize;
    public int loopsCount;
    public int loopsSize;
    public int desiredLoopSize;
    public int primesInALoop;
    public int maxPrimeThreshold;
    public int primeBaseSize;
    public int maxWheelPrime;
    public int maxBigPrimeSize;

    // -9152465561093747545 140%

    public void initN(BigInteger N) {
        primeBaseSize = (int) (6.71 * Math.pow(1.0336, N.bitLength()));
    }

    public void init() {
        maxPrimeThreshold = (int) Math.pow(PrimeBase.instance.maxPrime, 0.8);
        maxBigPrimeSize = (int) Math.pow(PrimeBase.instance.maxPrime, 1.3);
        loopsCount = 10;
        desiredLoopSize = PrimeBase.instance.maxPrime << 1;
        primesInALoop = 3;

        int maxWheelPrimeIndex = 0;
        List<Integer> primeBase = PrimeBase.instance.primeBase;

        int loopSize = 1;
        for (int i = 1; i <= primesInALoop; i++) {
            loopSize *= primeBase.get(i);
        }
        int oldLoop = 1;
        int size = primeBase.size();
        for (int i = 4; i < size; i++) {
            int prime = primeBase.get(i);
            if (loopSize > desiredLoopSize) {
                if (loopSize - desiredLoopSize > desiredLoopSize - oldLoop) {
                    loopSize = oldLoop;
                    maxWheelPrimeIndex = i - 2;
                } else {
                    maxWheelPrimeIndex = i - 1;
                }
                break;
            } else {
                oldLoop = loopSize;
                loopSize = loopSize * prime / primeBase.get(i - 3);
            }
        }


        this.maxWheelPrime = primeBase.get(maxWheelPrimeIndex);
        this.minPrimeSize = primeBase.get(maxWheelPrimeIndex - 3) + 1;
        this.loopsSize = loopSize;


        log();
        log("Specific settings");
        log("primeBaseSize", primeBaseSize);
        log("desiredLoopSize", Logger.formatLong(desiredLoopSize));
        log("loopsCount", Logger.formatLong(loopsCount));
        log("maxPrimeThreshold", Logger.formatLong(maxPrimeThreshold));
        log("maxBigPrimeSize", Logger.formatLong(maxBigPrimeSize));

        log();
        log("Implicit settings");
        log("loopsSize", Logger.formatLong(loopsSize));
        log("minPrimeSize", Logger.formatLong(minPrimeSize));
        log("maxWheelPrime", Logger.formatLong(maxWheelPrime));
    }
}
