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
        final double maxPrime = PrimeBase.instance.maxPrime.doubleValue();
        final double deltaLog = Math.log(maxPrime * MagicNumbers.instance.maxPrimeThreshold);
        final byte[] logs = new byte[MagicNumbers.instance.loopsSize];
        final List<BSmoothData> bSmoothList = new ArrayList<>();
        PolynomialData polynomialData = null;
        while (true) {
            // it takes time for the queue to fill up, so don't run statistics before the first item
            if(polynomialData != null) {
                Logger.SIEVER_TOTAL.start();
                try {
                    Logger.SIEVE_QUEUE_OUT.start();
                    polynomialData = DataQueue.polynomialData.take();
                    Logger.SIEVE_QUEUE_OUT.end();
                } catch (InterruptedException e) {
                    return;
                }
            }
            else{
                try {
                    polynomialData = DataQueue.polynomialData.take();
                } catch (InterruptedException e) {
                    return;
                }
                Logger.SIEVER_TOTAL.start();
            }

            long x = polynomialData.delta;

            for (int j = 0; j < MagicNumbers.instance.loopsCount; j++) {
                byte baseLog = calculateBaseLog(deltaLog, polynomialData, x);
                Logger.SIEVE_CORE.start();
                for (Wheel wheel : polynomialData.wheels) {
                    wheel.update(logs);
                }
                Logger.SIEVE_CORE.end();
                Logger.SIEVE_COLLECT.start();
                for (int i = 0; i < logs.length; i++) {
                    if (logs[i] >= baseLog) {
                        bSmoothList.add(BSmoothDataPool.instance.get(i));
                    }
                    logs[i] = 0;
                }
                Logger.SIEVE_COLLECT.end();

                x += logs.length;
            }

//            x = polynomialData.delta;
//            for (int j = 0; j < MagicNumbers.instance.loopsCount; j++) {
//                for (Wheel wheel : polynomialData.wheels) {
//                    wheel.update(logs);
//                }
//
//                x += logs.length;
//            }


            Logger.SIEVER_TOTAL.end();

            Logger.VE_TOTAL.start();
            vectorExtractor.extract(polynomialData, bSmoothList);
            Logger.VE_TOTAL.end();
            bSmoothList.clear();
            Logger.SIEVER_TOTAL.start();
            WheelPool.instance.put(polynomialData.wheels);
            Logger.SIEVER_TOTAL.end();
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
