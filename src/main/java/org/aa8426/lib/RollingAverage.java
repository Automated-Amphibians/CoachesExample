package org.aa8426.lib;

import java.util.Arrays;


public class RollingAverage {
    private final int windowSize;
    private final double outlierThreshold;
    private final double[] values;
    private int index = 0;
    private int count = 0;
    private double sum = 0;
    private double avg = 0.0;
    public boolean debug = false;
        
    
    public RollingAverage(int windowSize, double outlierThreshold) {
        this.windowSize = windowSize;

        if (windowSize <= 0) {
            throw new IllegalArgumentException("Window size must be greater than zero.");
        }
        this.outlierThreshold = outlierThreshold;
        this.values = new double[windowSize];
        reset();        
    }
    
    public void addValue(double newValue) {        
        if (count > 0 && Math.abs(newValue - avg) > outlierThreshold) {
            //System.out.println("tossed");
            return; // Discard outlier
        }

        sum = (sum - values[index]) + newValue;                
        values[index] = newValue;
        index = (index + 1) % windowSize;
        if (count < windowSize) {
            count++;
        }
        avg = sum / count;
        if (debug)
            System.out.println(String.format("Avg: %.2f, Sum: %.2f idx=%d count=%d", avg, sum, index, count));
    }
    
    public double getAverage() {
        return avg;
    }

    public double getRollingDistance() {
        for (int i = 0; i < values.length; i++) {
            //addValue(rcTest.Vision.getDistanceFromAprilTag(21));
        }
        return getAverage();
    }
    
    public static void main(String[] args) {
        RollingAverage avg = new RollingAverage(10, 5); // change outlier threshold
        avg.getRollingDistance();    
    }

    public void reset() {
        Arrays.fill(values, 0.0);        
        this.sum = 0;
        this.avg = 0;
        this.count = 0;
    }
}
