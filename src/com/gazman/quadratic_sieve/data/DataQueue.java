package com.gazman.quadratic_sieve.data;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DataQueue {
    public static BlockingQueue<PolynomialData> polynomialData = new ArrayBlockingQueue<>(Runtime.getRuntime().availableProcessors() * 2);
    public static BlockingQueue<BSmooth> bSmooths = new ArrayBlockingQueue<>(Runtime.getRuntime().availableProcessors() * 10);
}
