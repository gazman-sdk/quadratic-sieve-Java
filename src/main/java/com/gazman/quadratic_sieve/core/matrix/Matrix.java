package com.gazman.quadratic_sieve.core.matrix;

import com.gazman.quadratic_sieve.data.BSmooth;
import com.gazman.quadratic_sieve.data.DataQueue;
import com.gazman.quadratic_sieve.logger.Analytics;
import com.gazman.quadratic_sieve.logger.Logger;

import java.math.BigInteger;

/**
 * Responsible for sieving
 */
public class Matrix implements Runnable {

    public static final Matrix instance = new Matrix();

    private final GaussianEliminationMatrix matrix = new GaussianEliminationMatrix();
    private int bigPrimes = 0;

    public void start(BigInteger N) {
        matrix.setN(N);
        new Thread(this, "Matrix").start();
    }

    @Override
    public void run() {
        while (true) {
            BSmooth bSmooth;
            try {
                bSmooth = DataQueue.bSmooths.take();
            } catch (InterruptedException e) {
                return;
            }
            Analytics.MATRIX.start();
            if (bSmooth.bigPrime) {
                bigPrimes++;
            }
            Logger.logProgress();
            matrix.add(bSmooth);
            Analytics.MATRIX.end();
        }
    }

    public int getTotal() {
        return matrix.getSize();
    }

    public int getBigPrimes() {
        return bigPrimes;
    }
}
