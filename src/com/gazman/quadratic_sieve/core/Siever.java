package com.gazman.quadratic_sieve.core;

import com.gazman.quadratic_sieve.core.poly.WheelPool;
import com.gazman.quadratic_sieve.data.*;
import com.gazman.quadratic_sieve.logger.Logger;
import com.gazman.quadratic_sieve.wheel.Wheel;

import java.math.BigInteger;

/**
 * Responsible for sieving
 */
public class Siever implements Runnable {

    public void start() {
        new Thread(this,"Siever").start();
    }

    @Override
    public void run() {
        BigInteger loopsSize = MagicNumbers.instance.loopsSize;
        double[] logs = new double[loopsSize.intValue()];
        double maxPrime = PrimeBase.instance.maxPrime.doubleValue();
        double deltaLog = Math.log(maxPrime * MagicNumbers.instance.maxPrimeThreshold);
        //noinspection InfiniteLoopStatement
        while (true) {
            PolynomialData polynomialData;
            try {
                polynomialData = DataQueue.polynomialData.take();
            } catch (InterruptedException e) {
                throw new Error(e);
            }

            Logger.SIEVER.start();
            BigInteger x = BigInteger.ZERO;
            BigInteger c = polynomialData.getC();

            for (int j = 0; j < MagicNumbers.instance.loopsCount; j++) {
                double baseValue = polynomialData.getSievingValue(x, c).doubleValue();
                if (baseValue < 0) {
                    baseValue *= -1;
                }
                double baseLog = Math.log(baseValue) - deltaLog;
                for (Wheel wheel : polynomialData.wheels) {
                    wheel.update(logs);
                }

                for (int i = 0; i < logs.length; i++) {
                    if (logs[i] > baseLog || Math.abs(logs[i] - baseLog) < 0.0001) {
                        BigInteger localX = x.add(BigInteger.valueOf(i));
                        try {
                            DataQueue.vectorWorkData.put(new VectorWorkData(polynomialData, localX, c));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    logs[i] = 0;
                }

                x = x.add(loopsSize);
            }
            for (Wheel wheel : polynomialData.wheels) {
                WheelPool.instance.put(wheel);
            }
            Logger.SIEVER.end();
        }
    }
}
