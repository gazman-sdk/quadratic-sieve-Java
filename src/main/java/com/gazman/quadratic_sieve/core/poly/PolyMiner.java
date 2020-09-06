package com.gazman.quadratic_sieve.core.poly;

import com.gazman.quadratic_sieve.data.DataQueue;
import com.gazman.quadratic_sieve.data.PolynomialData;

import java.math.BigInteger;

/**
 * Responsible for generating multiple polynomial of the form (ax+b)^2-N
 */
public class PolyMiner implements Runnable {
    public static final PolyMiner instance = new PolyMiner();
    private BigInteger N;

    public void start(BigInteger N) {
        this.N = N;
        new Thread(this, "PolyMiner").start();
    }

    @Override
    public void run() {
        BigInteger a = N.sqrt(),c,x;

        double scale = 0;

        while (true) {
            a = a.nextProbablePrime();
            c = a.nextProbablePrime();
            x = a.multiply(c).subtract(N);
            BigInteger iStart = N.subtract(x).divide(a).add(BigInteger.ONE);

            if(scale == 0){
                scale = 256 / 2.0 / Math.log(iStart.multiply(a).add(x).subtract(N).doubleValue());
            }

            try {
                DataQueue.polynomialData.put(new PolynomialData(N, a,c,x, iStart, scale));
            } catch (InterruptedException e) {
                return;
            }
        }


    }
}

