package com.gazman.quadratic_sieve.logger;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import static com.gazman.quadratic_sieve.QuadraticSieve.DEBUG;

public final class Logger {

    private static final long startTime = System.nanoTime();
    private static boolean logsAvailable = true;
    private final AtomicLong totalTimeNano = new AtomicLong();

    public long getTotalTimeNano() {
        return totalTimeNano.get();
    }

    public static void setLogsAvailable(boolean logsAvailable) {
        Logger.logsAvailable = logsAvailable;
    }

    public static void log(Object...objects) {
        if(!DEBUG){
            return;
        }
        if(!logsAvailable){
            return;
        }
        StringBuilder out = new StringBuilder();
        for (Object object : objects) {
            out.append(object).append(" ");
        }

        long milliseconds = (System.nanoTime() - startTime) / 1_000_000;

        StringBuilder prefix = new StringBuilder(formatLong(milliseconds) + "> ");
        while (prefix.length() < 8){
            prefix.append(" ");
        }
        System.out.println(prefix.toString() + out);
    }

    public static String formatDouble(double value, int spaces) {
        return String.format("%." + spaces + "f", value);
    }

    public static String formatLong(long value){
        return String.format("%,d", value);
    }

    public static String formatTime(long timeMillis){
        Duration timeLeft = Duration.ofMillis(timeMillis);
        if(timeLeft.toDays() > 0) {
            return String.format("%d days, %02d:%02d:%02d",
                    timeLeft.toDays(), timeLeft.toHours()  % 24, timeLeft.toMinutesPart(), timeLeft.toSecondsPart());
        }
        if(timeLeft.toHours() > 0){
            return String.format("%02d:%02d:%02d",
                    timeLeft.toHours(), timeLeft.toMinutesPart(), timeLeft.toSecondsPart());
        }
        else{
            return String.format("%02d:%02d",
                    timeLeft.toMinutesPart(), timeLeft.toSecondsPart());
        }
    }
}
