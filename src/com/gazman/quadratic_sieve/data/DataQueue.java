package com.gazman.quadratic_sieve.data;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DataQueue {
    public static BlockingQueue<PolynomialData> polynomialData = new ArrayBlockingQueue<>(10);
    public static BlockingQueue<VectorWorkData> vectorWorkData = new ArrayBlockingQueue<>(1000);
    public static BlockingQueue<BSmooth> bSmooths = new ArrayBlockingQueue<>(1000);
}
