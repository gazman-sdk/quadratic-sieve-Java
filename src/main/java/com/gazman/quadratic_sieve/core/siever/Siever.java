package com.gazman.quadratic_sieve.core.siever;

import com.gazman.quadratic_sieve.core.poly.WheelPool;
import com.gazman.quadratic_sieve.data.DataQueue;
import com.gazman.quadratic_sieve.data.MagicNumbers;
import com.gazman.quadratic_sieve.data.PolynomialData;
import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.debug.Analytics;
import com.gazman.quadratic_sieve.wheel.Wheel;

import java.math.BigInteger;
import java.util.ArrayList;
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
        final byte[] logs = new byte[MagicNumbers.instance.loopsSize];
        final List<BSmoothData> bSmoothList = new ArrayList<>();
        PolynomialData polynomialData = null;
        while (true) {
            // it takes time for the queue to fill up, so don't run statistics before the first item
            if (polynomialData != null) {
                try {
                    Analytics.SIEVE_QUEUE_OUT.start();
                    polynomialData = DataQueue.polynomialData.take();
                    Analytics.SIEVE_QUEUE_OUT.end();
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

            Analytics.SIEVER_WHEELS.start();
            List<Wheel> wheels = polynomialData.buildWheels();
            Analytics.SIEVER_WHEELS.end();
            for (int j = 0; j < MagicNumbers.instance.loopsCount; j++) {
                byte baseLog = calculateBaseLog(deltaLog, polynomialData, x);
                Analytics.SIEVE_CORE.start();
                for (Wheel wheel : wheels) {
                    wheel.update(logs);
                }
                Analytics.SIEVE_CORE.end();
                Analytics.SIEVE_COLLECT.start();
                for (int i = 0; i < logs.length; i++) {
                    if (logs[i] >= baseLog) {
                        bSmoothList.add(BSmoothDataPool.instance.get(x + i));
                    }
                    logs[i] = 0;
                }
                Analytics.SIEVE_COLLECT.end();

                x += logs.length;
            }

            Analytics.SIEVE_RE_SIEVE.start();
            for (Wheel wheel : wheels) {
                wheel.updateSmooth(bSmoothList);
            }

            for (BSmoothData bSmoothData : bSmoothList) {
                BigInteger sievingValue = polynomialData.getSievingValue(bSmoothData.localX);
                BigInteger reminder = sievingValue.divide(bSmoothData.bigValue == null ? BigInteger.valueOf(bSmoothData.value) : bSmoothData.bigValue);
                bSmoothData.reminder = reminder.longValue();
            }
            Analytics.SIEVE_RE_SIEVE.end();


            Analytics.VECTOR_EXTRACTOR_TOTAL.start();
            vectorExtractor.extract(polynomialData, bSmoothList);
            Analytics.VECTOR_EXTRACTOR_TOTAL.end();
            bSmoothList.clear();
            WheelPool.instance.put(wheels);
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
