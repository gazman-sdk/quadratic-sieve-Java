package com.gazman.quadratic_sieve.debug;

import com.gazman.quadratic_sieve.core.matrix.Matrix;
import com.gazman.quadratic_sieve.data.PrimeBase;
import com.gazman.quadratic_sieve.primes.BigPrimes;

import java.time.Duration;
import java.util.Arrays;

public final class Logger {

    private static final long startTime = System.nanoTime();
    private static final long startTimeNano = System.nanoTime();
    private static long lastUpdateNano = System.nanoTime();

    public static void log(Object... objects) {
        StringBuilder out = new StringBuilder(objects.length << 3);
        for (Object object : objects) {
            out.append(object).append(" ");
        }

        long milliseconds = (System.nanoTime() - startTime) / 1_000_000;

        StringBuilder prefix = new StringBuilder(formatLong(milliseconds) + "> ");
        while (true) {
            int length = prefix.length();
            if (!(length < 8)) break;
            prefix.append(" ");
        }
        System.out.println(prefix.toString() + out);
    }

    public static void logProgress() {
        logProgress(false);
    }

    public static void logProgress(boolean force) {
        if (force || System.nanoTime() - lastUpdateNano > 1_000_000_000) {
            lastUpdateNano = System.nanoTime();
            int bSmoothFound = Matrix.instance.getTotal();
            double totalBSmoothValues = PrimeBase.instance.primeBase.size() * 0.95;
            double progress = bSmoothFound / totalBSmoothValues;

            long timePast = System.nanoTime() - startTimeNano;
            double speedInMilliseconds = 1_000_000.0 * bSmoothFound / timePast;
            BigPrimes bigPrimes = BigPrimes.instance;
            int usedBigPrimesCount = Matrix.instance.getBigPrimes();
            log("Completed", Logger.formatDouble(progress * 100, 2) + "%",
                    "bSmooth-found", Logger.formatLong(bSmoothFound - usedBigPrimesCount),
                    "big-primes-found", Logger.formatLong(usedBigPrimesCount) + "/" +
                            Logger.formatLong(bigPrimes.getTotalBigPrimes()),
                    Arrays.asList(Analytics.values()),
                    "TTD", Logger.formatTime((long) ((totalBSmoothValues - bSmoothFound) / speedInMilliseconds)));
        }
    }

    public static String formatDouble(double value, int spaces) {
        //noinspection StringConcatenationMissingWhitespace,StringConcatenationInFormatCall,StringConcatenationInFormatCall
        return String.format("%." + spaces + "f", value);
    }

    public static String formatLong(long value) {
        return String.format("%,d", value);
    }

    public static String formatTime(long timeMillis) {
        Duration timeLeft = Duration.ofMillis(timeMillis);
        if (timeLeft.toDays() > 0) {
            return String.format("%d days, %02d:%02d:%02d",
                    timeLeft.toDays(), timeLeft.toHours() % 24, timeLeft.toMinutesPart(), timeLeft.toSecondsPart());
        }
        if (timeLeft.toHours() > 0) {
            return String.format("%02d:%02d:%02d",
                    timeLeft.toHours(), timeLeft.toMinutesPart(), timeLeft.toSecondsPart());
        } else {
            return String.format("%02d:%02d",
                    timeLeft.toMinutesPart(), timeLeft.toSecondsPart());
        }
    }
}
