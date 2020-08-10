package com.gazman.quadratic_sieve.core;

import com.gazman.quadratic_sieve.data.*;
import com.gazman.quadratic_sieve.logger.Logger;
import com.gazman.quadratic_sieve.wheel.Wheel;

import java.math.BigInteger;

/**
 * Responsible for sieving
 */
public class Siever implements Runnable {

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        double[] logs = new double[MagicNumbers.instance.loopsSize.intValue()];
        double maxPrime = PrimeBase.instance.maxPrime.doubleValue();
        BigInteger loopsSize = MagicNumbers.instance.loopsSize;
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
                double baseLog = Math.log(polynomialData.getSievingValue(x, c).doubleValue()) -
                        Math.log(maxPrime * MagicNumbers.instance.maxPrimeThreshold);
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
            Logger.SIEVER.end();
        }
    }
}
