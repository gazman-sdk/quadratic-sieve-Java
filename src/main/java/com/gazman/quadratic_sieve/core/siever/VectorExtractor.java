package com.gazman.quadratic_sieve.core.siever;

import com.gazman.quadratic_sieve.data.BSmooth;
import com.gazman.quadratic_sieve.data.DataQueue;
import com.gazman.quadratic_sieve.data.PolynomialData;
import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.debug.Analytics;
import com.gazman.quadratic_sieve.primes.BigPrimes;

import java.util.List;

/**
 * Responsible for sieving
 */
public class VectorExtractor {

    public void extract(PolynomialData polynomialData, List<BSmoothData> bSmoothList) {
        for (BSmoothData bSmoothData : bSmoothList) {
            if (bSmoothData.reminder < 0) {
                bSmoothData.vector.set(0);
                bSmoothData.reminder *= -1;
            }
            int powerTwo = Long.numberOfTrailingZeros(bSmoothData.reminder);
            if ((powerTwo & 0x1) == 1) {
                bSmoothData.vector.set(1);
            }
            bSmoothData.reminder >>= powerTwo;

            List<Integer> primeBase = PrimeBase.instance.primeBase;
            for (int i = 2; i < primeBase.size(); i++) {
                int prime = primeBase.get(i);
                if (prime > bSmoothData.reminder) {
                    break;
                }
                if (bSmoothData.reminder % prime == 0) {
                    int count = 0;
                    do {
                        bSmoothData.reminder /= prime;
                        count++;
                    } while (bSmoothData.reminder % prime == 0);
                    if ((count & 0x1) == 1) {
                        bSmoothData.vector.flip(i);
                    }
                }
            }
            BSmooth bSmooth = new BSmooth(polynomialData, bSmoothData.localX, bSmoothData.vector);
            if (bSmoothData.reminder == 1) {
                try {
                    Analytics.VECTOR_EXTRACTOR_QUEUE.start();
                    DataQueue.bSmooths.put(bSmooth);
                    Analytics.VECTOR_EXTRACTOR_QUEUE.end();
                } catch (InterruptedException e) {
                    return;
                }
            } else {
                BigPrimes.instance.addBigPrime(bSmooth, bSmoothData.reminder);
            }
        }
    }


}
