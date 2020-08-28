package com.gazman.quadratic_sieve.core.poly;

import com.gazman.quadratic_sieve.data.PolynomialData;
import com.gazman.quadratic_sieve.wheel.Wheel;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class WheelPool {
    private final Map<PolynomialData, Queue<List<Wheel>>> wheelMap = Collections.synchronizedMap(new HashMap<>());

    public static final WheelPool instance = new WheelPool();

    public List<Wheel> get(PolynomialData polynomialData) {
        Queue<List<Wheel>> wheelLists = wheelMap.get(polynomialData);
        return wheelLists != null ? wheelLists.poll() : null;
    }

    public void put(PolynomialData polynomialData, List<Wheel> wheels) {
        wheelMap.computeIfAbsent(polynomialData, x -> new ConcurrentLinkedDeque<>()).add(wheels);
    }

}
