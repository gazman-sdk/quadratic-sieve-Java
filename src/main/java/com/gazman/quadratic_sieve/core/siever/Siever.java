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
import java.util.List;

/**
 * Responsible for sieving
 */
public class Siever implements Runnable {

    public void start() {
        new Thread(this, "Siever").start();
    }

    @Override
    public void run() {
        final double maxPrime = PrimeBase.instance.maxPrime.doubleValue();
        final double deltaLog = Math.log(maxPrime * MagicNumbers.instance.maxPrimeThreshold);

        int loopSize = MagicNumbers.instance.loopsSize;
        int loopsCount = MagicNumbers.instance.loopsCount;
        final ByteArray logs = new ByteArray(loopSize);


        final List<BSmoothData> bSmoothList = new ArrayList<>(256);
        PolynomialData polynomialData = null;
        byte baseLog = 0;
        long mask = 0;
        while (true) {
            polynomialData = next(polynomialData);
            if (polynomialData == null) {
                return;
            }

            List<Wheel> wheels = buildWheels(polynomialData);

            logs.clear();


            if (baseLog == 0) {
                baseLog = calculateBaseLog(deltaLog, polynomialData, 0);
                mask = buildMask(baseLog);
            }
            for (int j = 0, x = 0; j < loopsCount; j++, x += loopSize) {
                Analytics.start();
                for (int i = 0, wheelsSize = wheels.size(); i < wheelsSize; i++) {
                    Wheel wheel = wheels.get(i);
                    wheel.update(logs);
                }
                Analytics.SIEVE_CORE.end();
                collect(loopSize, logs, bSmoothList, baseLog, mask, x);
            }

            receive(bSmoothList, polynomialData, wheels);

            extractVector(bSmoothList, polynomialData, wheels);
        }
    }

    private static void extractVector(List<BSmoothData> bSmoothList, PolynomialData polynomialData, List<Wheel> wheels) {
        Analytics.start();
        VectorExtractor.extract(polynomialData, bSmoothList);
        bSmoothList.clear();
        WheelPool.instance.put(wheels);
        Analytics.SIEVE_VECTOR_EXTRACTOR.end();
    }

    private static void receive(List<BSmoothData> bSmoothList, PolynomialData polynomialData, List<Wheel> wheels) {
        Analytics.start();
        for (int i = 0, wheelsSize = wheels.size(); i < wheelsSize; i++) {
            Wheel wheel = wheels.get(i);
            wheel.updateSmooth(bSmoothList);
        }
        Analytics.SIEVE_RE_SIEVE_1.end();

        Analytics.start();
        for (BSmoothData bSmoothData : bSmoothList) {
            BigInteger sievingValue, reminder;

            sievingValue = polynomialData.getSievingValueA(bSmoothData.localX);
            reminder = calculateReminder(bSmoothData.valueA, sievingValue);
            if (reminder.bitLength() < 64) {
                bSmoothData.reminderA = reminder.longValue();
            } else {
                bSmoothData.ignoreA = true;
            }

            sievingValue = polynomialData.getSievingValueB(bSmoothData.localX);
            reminder = calculateReminder(bSmoothData.valueB, sievingValue);
            if (reminder.bitLength() < 64) {
                bSmoothData.reminderB = reminder.longValue();
            } else {
                bSmoothData.ignoreB = true;
            }
        }
        Analytics.SIEVE_RE_SIEVE_2.end();
    }

    private static BigInteger calculateReminder(long[] valueA, BigInteger sievingValue) {
        BigInteger v0 = BigInteger.valueOf(valueA[0]);
        BigInteger v1 = BigInteger.valueOf(valueA[1]);
        BigInteger v2 = BigInteger.valueOf(valueA[2]);
        return sievingValue.divide(v0.multiply(v1).multiply(v2));
    }

    private static void collect(int loopSize, ByteArray logs, List<BSmoothData> bSmoothList, byte baseLog, long mask, int x) {
        Analytics.start();
        for (int i = 0; i < loopSize; i += 8) {
            if ((logs.getLong(i) & mask) > 0) {
                for (int k = 0; k < 8; k++) {
                    if (logs.getByte(i + k) >= baseLog) {
                        bSmoothList.add(BSmoothDataPool.get(x + i + k));
                    }
                }
            }
        }
        logs.clear();
        Analytics.SIEVE_COLLECT.end();
    }

    private static long buildMask(byte baseLog) {
        long mask;
        byte byteMask = (byte) Integer.highestOneBit(baseLog);
        byteMask |= byteMask << 1;

        mask = ByteBuffer.wrap(new byte[]{byteMask,
                byteMask, byteMask, byteMask, byteMask, byteMask, byteMask, byteMask}).getLong();
        return mask;
    }

    private static List<Wheel> buildWheels(PolynomialData polynomialData) {
        Analytics.start();
        List<Wheel> wheels = polynomialData.buildWheels();
        Analytics.SIEVER_BUILD_WHEELS.end();
        return wheels;
    }

    private static PolynomialData next(PolynomialData polynomialData) {
        // it takes time for the queue to fill up, so don't run statistics before the first item
        if (polynomialData != null) {
            try {
                Analytics.start();
                polynomialData = DataQueue.polynomialData.take();
                Analytics.SIEVE_QUEUE_POLY.end();
            } catch (InterruptedException e) {
                return null;
            }
        } else {
            try {
                polynomialData = DataQueue.polynomialData.take();
            } catch (InterruptedException e) {
                return null;
            }
        }
        return polynomialData;
    }

    private static byte calculateBaseLog(double deltaLog, PolynomialData polynomialData, int x) {
        double baseValue = polynomialData.getSievingValueA(x).doubleValue();
        if (baseValue < 0) {
            baseValue *= -1;
        }
        return (byte) Math.round((Math.log(baseValue) - deltaLog) * polynomialData.scale);
    }
}
