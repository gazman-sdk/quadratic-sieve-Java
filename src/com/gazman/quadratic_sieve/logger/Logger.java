package com.gazman.quadratic_sieve.logger;

public enum Logger {
    ;

    private static final long startTime = System.nanoTime();
    private static boolean logsAvailable = true;

    public static void setLogsAvailable(boolean logsAvailable) {
        Logger.logsAvailable = logsAvailable;
    }

    public static void log(Object...objects) {
        if(!logsAvailable){
            return;
        }
        StringBuilder out = new StringBuilder();
        for (Object object : objects) {
            out.append(object).append(" ");
        }

        long milliseconds = (System.nanoTime() - startTime) / 1_000_000;

        String prefix = milliseconds + "> ";
        System.out.println(prefix + out);
    }
}
