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
        double maxPrime = PrimeBase.instance.maxPrime.doubleValue();
        double deltaLog = Math.log(maxPrime * MagicNumbers.instance.maxPrimeThreshold);
        double[] logs = null;
        List<BSmoothData> bSmoothList = new ArrayList<>();
        //noinspection InfiniteLoopStatement
        while (true) {
            PolynomialData polynomialData;
            try {
                polynomialData = DataQueue.polynomialData.take();
            } catch (InterruptedException e) {
                throw new Error(e);
            }

            if (logs == null) {
                logs = new double[polynomialData.getLoopsSize()];
            }

            long x = 0;

            for (int j = 0; j < MagicNumbers.instance.loopsCount; j++) {
                Logger.SIEVER.start();
                bSmoothList.clear();
                double baseValue = polynomialData.getSievingValue(x).doubleValue();
                if (baseValue < 0) {
                    baseValue *= -1;
                }
                double baseLog = Math.log(baseValue) - deltaLog;
                for (Wheel wheel : polynomialData.wheels) {
                    wheel.update(logs);
                }

                for (int i = 0; i < logs.length; i++) {
                    if (logs[i] > baseLog || Math.abs(logs[i] - baseLog) < 0.0001) {
                        bSmoothList.add(BSmoothDataPool.instance.get(x + i, logs[i]));
                    }
                    logs[i] = 0;
                }

                x += logs.length;
                Logger.SIEVER.end();
                vectorExtractor.extract(polynomialData, bSmoothList);
            }

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
