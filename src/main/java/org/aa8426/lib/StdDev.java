package org.aa8426.lib;

import java.util.LinkedList;
import java.util.Queue;

public class StdDev {
    private final int capacity;
    private final Queue<Double> numbers;
    private double sum;
    private double sumOfSquares;

    public StdDev(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        this.capacity = capacity;
        this.numbers = new LinkedList<>();
        this.sum = 0;
        this.sumOfSquares = 0;
    }

    public void addNumber(double number) {
        if (numbers.size() == capacity) {
            double removed = numbers.poll();
            sum -= removed;
            sumOfSquares -= removed * removed;
        }
        numbers.add(number);
        sum += number;
        sumOfSquares += number * number;
    }

    public double getStandardDeviation() {
        int size = numbers.size();
        if (size == 0) {
            throw new IllegalStateException("No numbers available to calculate standard deviation");
        }
        double mean = sum / size;
        double variance = (sumOfSquares / size) - (mean * mean);
        return Math.sqrt(variance);
    }

    public static void testCase(double[] test) {
        StdDev deviation = new StdDev(test.length);
        for(double number: test) {
            deviation.addNumber(number);
        }
        System.out.println("Standard Deviation: " + deviation.getStandardDeviation());
    }

    public static void main(String[] args) {
        testCase(new double[] {1,2,3,4,5});
        testCase(new double[] {1,2,3,4,5,6,7,8,9,10});
        testCase(new double[] {6,7,8,9,10});
        testCase(new double[] {0.1,0.2,0.5,0.8,0.9});
    }

}