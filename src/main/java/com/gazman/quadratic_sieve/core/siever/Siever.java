package com.gazman.quadratic_sieve.core.siever;

import com.gazman.quadratic_sieve.core.poly.WheelPool;
import com.gazman.quadratic_sieve.data.DataQueue;
import com.gazman.quadratic_sieve.data.MagicNumbers;
import com.gazman.quadratic_sieve.data.PolynomialData;
import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.debug.Analytics;
import com.gazman.quadratic_sieve.utils.ByteArray;
import com.gazman.quadratic_sieve.wheel.Wheel;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Responsible for sieving
 */
public class Siever implements Runnable {

    private final VectorExtractor vectorExtractor = new VectorExtractor();

    public void start() {
        new Thread(this, "Siever").start();
    }

    @Override
    public void run() {
        final double maxPrime = PrimeBase.instance.maxPrime.doubleValue();
        final double deltaLog = Math.log(maxPrime * MagicNumbers.instance.maxPrimeThreshold);

        int loopSize = MagicNumbers.instance.loopsSize;
        final ByteArray baseLogs = new ByteArray(loopSize);
        final ByteArray logs = new ByteArray(loopSize);

        int loopsCount = MagicNumbers.instance.loopsCount * MagicNumbers.instance.loopsSize / loopSize;

        final List<BSmoothData> bSmoothList = new ArrayList<>();
        PolynomialData polynomialData = null;
        byte baseLog = 0;
        long mask = 0;
        while (true) {
            // it takes time for the queue to fill up, so don't run statistics before the first item
            if (polynomialData != null) {
                try {
                    Analytics.SIEVE_QUEUE_POLY.start();
                    polynomialData = DataQueue.polynomialData.take();
                    baseLogs.clear();
                    Analytics.SIEVE_QUEUE_POLY.end();
                } catch (InterruptedException e) {
                    return;
                }
            } else {
                try {
                    polynomialData = DataQueue.polynomialData.take();
                } catch (InterruptedException e) {
                    return;
                }
            }

            long x = polynomialData.delta;

            Analytics.SIEVER_BUILD_WHEELS.start();
            List<Wheel> wheels = polynomialData.buildWheels();
            Analytics.SIEVER_BUILD_WHEELS.end();

            Analytics.SIEVER_STATIC_WHEELS.start();
            int wheelStartingPosition = 0;
            int maxWheelPrime = MagicNumbers.instance.maxWheelPrime;
            for (int i = 0, wheelsSize = wheels.size(); i < wheelsSize; i++) {
                Wheel wheel = wheels.get(i);
                if (wheel.prime > maxWheelPrime) {
                    break;
                }
                wheel.update(baseLogs);
                wheelStartingPosition++;
            }
            Analytics.SIEVER_STATIC_WHEELS.end();

            logs.clear(baseLogs);


            if (baseLog == 0) {
                baseLog = calculateBaseLog(deltaLog, polynomialData, x);
                byte byteMask = (byte) Integer.highestOneBit(baseLog);
                byteMask |= byteMask << 1;

                mask = ByteBuffer.wrap(new byte[]{byteMask,
                        byteMask, byteMask, byteMask, byteMask, byteMask, byteMask, byteMask}).getLong();
            }
            BitSet vector = polynomialData.aData.vector;
            for (int j = 0; j < loopsCount; j++) {
                Analytics.SIEVE_CORE.start();
                for (int i = wheelStartingPosition, wheelsSize = wheels.size(); i < wheelsSize; i++) {
                    wheels.get(i).update(logs);
                }
                Analytics.SIEVE_CORE.end();
                Analytics.SIEVE_COLLECT.start();
                for (int i = 0; i < loopSize; i += 8) {
                    if ((logs.getLong(i) & mask) > 0) {
                        for (int k = 0; k < 8; k++) {
                            if (logs.getByte(i + k) >= baseLog) {
                                bSmoothList.add(new BSmoothData(x + i + k, vector));
                            }
                        }
                    }
                }
                logs.clear(baseLogs);
                Analytics.SIEVE_COLLECT.end();

                x += loopSize;
            }

            Analytics.SIEVE_RE_SIEVE_1.start();
            for (int i = 0, wheelsSize = wheels.size(); i < wheelsSize; i++) {
                wheels.get(i).updateSmooth(bSmoothList);
            }
            Analytics.SIEVE_RE_SIEVE_1.end();

            Analytics.SIEVE_RE_SIEVE_2.start();
            for (BSmoothData bSmoothData : bSmoothList) {
                BigInteger sievingValue = polynomialData.getSievingValue(bSmoothData.localX);
                BigInteger v0 = BigInteger.valueOf(bSmoothData.value[0]);
                BigInteger v1 = BigInteger.valueOf(bSmoothData.value[1]);
                BigInteger v2 = BigInteger.valueOf(bSmoothData.value[2]);
                BigInteger reminder = sievingValue.divide(v0.multiply(v1).multiply(v2));
                bSmoothData.reminder = reminder.longValue();
            }
            Analytics.SIEVE_RE_SIEVE_2.end();

            Analytics.SIEVE_VECTOR_EXTRACTOR.start();
            vectorExtractor.extract(polynomialData, bSmoothList);
            Analytics.SIEVE_VECTOR_EXTRACTOR.end();
            bSmoothList.clear();
            WheelPool.instance.put(polynomialData, wheels);
        }
    }

    private byte calculateBaseLog(double deltaLog, PolynomialData polynomialData, long x) {
        double baseValue = polynomialData.getSievingValue(x).doubleValue();
        if (baseValue < 0) {
            baseValue *= -1;
        }
        return (byte) Math.round((Math.log(baseValue) - deltaLog) * polynomialData.scale);
    }
}
