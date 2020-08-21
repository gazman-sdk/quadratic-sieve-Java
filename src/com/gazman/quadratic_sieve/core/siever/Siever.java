package com.gazman.quadratic_sieve.core.siever;

import com.gazman.quadratic_sieve.core.poly.WheelPool;
import com.gazman.quadratic_sieve.data.DataQueue;
import com.gazman.quadratic_sieve.data.MagicNumbers;
import com.gazman.quadratic_sieve.data.PolynomialData;
import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.logger.Logger;
import com.gazman.quadratic_sieve.wheel.Wheel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Responsible for sieving
 */
public class Siever implements Runnable {

    private final VectorExtractor vectorExtractor = new VectorExtractor();
    private static final AtomicInteger polynomialsCounter = new AtomicInteger();

    public void start() {
        new Thread(this, "Siever").start();
    }

    @Override
    public void run() {
        final double maxPrime = PrimeBase.instance.maxPrime.doubleValue();
        final double deltaLog = Math.log(maxPrime * MagicNumbers.instance.maxPrimeThreshold);
        final byte[] logs = new byte[MagicNumbers.instance.loopsSize];
        final List<BSmoothData> bSmoothList = new ArrayList<>();
        while (true) {
            PolynomialData polynomialData;
            try {
                polynomialData = DataQueue.polynomialData.take();
            } catch (InterruptedException e) {
                return;
            }

            polynomialsCounter.getAndIncrement();
            long x = polynomialData.delta;

            for (int j = 0; j < MagicNumbers.instance.loopsCount; j++) {
                Logger.SIEVER.start();
                double baseValue = polynomialData.getSievingValue(x).doubleValue();
                if (baseValue < 0) {
                    baseValue *= -1;
                }
                byte baseLog = (byte) Math.round((Math.log(baseValue) - deltaLog) * polynomialData.scale);
                Logger.TEST.start();
                for (Wheel wheel : polynomialData.wheels) {
                    wheel.update(logs);
                }
                for (int i = 0; i < logs.length; i++) {
                    if (logs[i] >= baseLog) {
                        bSmoothList.add(BSmoothDataPool.instance.get(x + i, logs[i]));
                    }
                    logs[i] = 0;
                }
                Logger.TEST.end();

                x += logs.length;
                Logger.SIEVER.end();
            }

            Logger.SIEVER.setExtraInfo(polynomialsCounter.get());

            vectorExtractor.extract(polynomialData, bSmoothList);
            bSmoothList.clear();
            Logger.SIEVER.start();
            returnTheWheels(polynomialData);
            Logger.SIEVER.end();
        }
    }

    private void returnTheWheels(PolynomialData polynomialData) {
        for (Wheel wheel : polynomialData.wheels) {
            WheelPool.instance.put(wheel);
        }
    }
}
