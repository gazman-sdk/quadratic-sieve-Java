package com.gazman.quadratic_sieve.core.matrix;

import com.gazman.quadratic_sieve.data.BSmooth;
import com.gazman.quadratic_sieve.data.DataQueue;
import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.debug.Analytics;
import com.gazman.quadratic_sieve.debug.Logger;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Responsible for sieving
 */
public class Matrix implements Runnable {

    public static final Matrix instance = new Matrix();

    private final GaussianEliminationMatrix matrix = new GaussianEliminationMatrix();
    private int bigPrimes = 0;
    private List<BSmooth> smooths;

    public void start(BigInteger N) {
        matrix.setN(N);
        new Thread(this, "Matrix").start();
    }

    @Override
    public void run() {
        int flashSize = (int) (PrimeBase.instance.primeBase.size() * 0.5);
        smooths = new ArrayList<>(flashSize);
        Comparator<BSmooth> comparator = Comparator.comparingInt(o -> o.vector.length());
        while (true) {
            BSmooth bSmooth;
            try {
                bSmooth = DataQueue.bSmooths.take();
            } catch (InterruptedException e) {
                return;
            }
            Analytics.start();
            if (bSmooth.bigPrime) {
                bigPrimes++;
            }
            Logger.logProgress();
            if (smooths != null) {
                smooths.add(bSmooth);
                if (smooths.size() > flashSize) {
                    smooths.sort(comparator);

                    for (int i = smooths.size() - 1; i >= 0; i--) {
                        matrix.add(smooths.get(i));
                    }
                    smooths = null;
                }
            } else {
                matrix.add(bSmooth);
            }
            Analytics.MATRIX.end();
        }
    }

    public int getTotal() {
        return smooths != null ? smooths.size() : matrix.getSize();
    }

    public int getBigPrimes() {
        return bigPrimes;
    }
}
