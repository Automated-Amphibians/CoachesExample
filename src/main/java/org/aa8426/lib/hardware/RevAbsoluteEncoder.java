package org.aa8426.lib.hardware;

import org.aa8426.lib.dashboard.SendableFluent;
import org.aa8426.lib.dashboard.SendableFluent.ISendableFluent;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DutyCycleEncoder;

/**
 * The rev absolute encoder gives you values from 0-1 (including the 1!). It is CCW positive when looking at it head on, 
 * and vice versa from behind. 
 * 
 * Rev absolute encoders should be plugged into a DIO port (channel). They are generally perfectly stable up to two digits, 
 * and probably useful for 3. (so 1000 ticks of accuracy)
 * 
 * Offset should be set whatever the reading is at the first hardstop of the mechanism. 
 * Size represents the reading at the second hardstop and defines the range of the mechanism.
 * 
 * If your mechanism has no hardstops (moves in a circle or continuous), use 0 and 1 for size.
 * 
 */
public class RevAbsoluteEncoder implements ISendableFluent {
    
    public DigitalInput input;
    public DutyCycleEncoder absEncoder;
    private double scaleMin = 0.0;
    private double scaleMax = 100.0;
    private double offset = 0;
    private double size = 1;
    private boolean reverse = false;
            
    public RevAbsoluteEncoder(int channel) {
        input = new DigitalInput(channel);
        absEncoder = new DutyCycleEncoder(input);
        addSendables(SendableFluent.getInstance());        
    }

    public RevAbsoluteEncoder setScale(double min, double max) {
        this.scaleMax = max;
        this.scaleMin = min; 
        return this;       
    }

    public RevAbsoluteEncoder setOffsetAndSize(double offset, double size) {
        this.offset = offset;
        this.size = size;
        return this;
    }

    public RevAbsoluteEncoder setReverse(boolean reverse) {
        this.reverse = reverse;
        return this;
    }
    
    public double getOffset() {
        return offset;
    }
    
    public double getRawReading() {
        return absEncoder.get();
    }

    public double getAdjustedReading() {
        return getAdjustedReading(absEncoder.get(), offset, size, reverse);        
    }

    static public double getAdjustedReading(double val, double offset, double size, boolean reverse) {
        if (val == offset) {
            return reverse ? size : 0.0;
        }
        double half = offset - ((1 - size) / 2);
        double read;
              
        if (val > half) {
            read = val - offset;
        } else {
            read = val + (1 - offset);
        }
        return reverse ? size - read  : read;
    }

    public static double getScaledReading(double reading, double offset, double size, boolean reverse, double lowEnd, double highEnd) {
        double adjusted = getAdjustedReading(reading, offset, size, reverse);
        double scale = highEnd - lowEnd;
        double insidescale =  (1/size);
        double result = (adjusted * insidescale * scale) + lowEnd;
        //System.out.print(String.format("reading=%.2f, rev=%s, size=%.2f, adjusted=%.2f, is=%.2f, result=%.2f, low=%.2f, high=%.2f", reading, reverse+"", size, adjusted, insidescale, result, lowEnd, highEnd));

        return result;
    }


    public static void assertEquals(Double val1, Double val2) {
        if (Math.abs(val2 - val1) > 0.000001) {
            System.out.println("... Not equal! val1="+val1+","+"val2="+val2);
        } else {
            System.out.println("... pass (val1="+val1+","+"val2="+val2+")");
        }
    }

    public static void fullTest(double offset, double size, boolean reverse, double lowEnd, double highEnd) {
        System.out.println("------------");
        System.out.println(String.format("offset=%.2f, size=%.2f, reverse=%s, lowEnd=%.2f, highEnd=%.2f", offset, size, ""+reverse, lowEnd, highEnd));

        assertEquals(lowEnd, getScaledReading(offset, offset, size, reverse, lowEnd, highEnd));
        assertEquals(highEnd, getScaledReading(offset+size > 1 ? offset+size-1 : offset+size, offset, size, reverse, lowEnd, highEnd));
        assertEquals(lowEnd + ((highEnd - lowEnd)/2), getScaledReading(offset+(size/2) > 1 ? offset+(size/2)-1 : offset+(size/2), offset, size, reverse, lowEnd, highEnd));

        assertEquals(highEnd, getScaledReading(offset, offset, size, !reverse, lowEnd, highEnd));
        assertEquals(lowEnd, getScaledReading(offset+size > 1 ? offset+size-1 : offset+size, offset, size, !reverse, lowEnd, highEnd));
        //assertEquals(lowEnd + ((highEnd - lowEnd)/2), fullTestX(offset+(size/2) > 1 ? offset+(size/2)-1 : offset+(size/2), offset, size, reverse, lowEnd, highEnd));        
    }

    public static void main(String[] args) {
        fullTest(0.9, 0.25, false, 0, 90);
        fullTest(0.9, 0.25, false, 0, 1);
        fullTest(0.9, 0.25, false, 0, 1);
        fullTest(0.0, 1, false, 0, 1);
        fullTest(0.25, 0.50, false, 10, 11);
        fullTest(0.25, 0.50, false, -1, 0);
        fullTest(0.25, 0.50, false, -5, 5);
    }

    public double getScaledReading() {
        return getScaledReading(getRawReading(), offset, size, reverse, scaleMin, scaleMax);
    }

    @Override
    public SendableFluent addSendables(SendableFluent s) {                
        if (s.debugMode) {
            s.addDefaultKey("revabsencoder");                        
            s.addDouble("offset", this::getOffset, null);
            s.addDouble("size", () -> this.size, null);
            s.addBoolean("inverted", () -> reverse, null);
            s.addDouble("rawReading", () -> this.getRawReading(), null);
            s.addDouble("adjustedRead", this::getAdjustedReading, null);
            s.addDouble("scaledRead", this::getScaledReading, null);            
            s.removeDefaultKey();
        }
        return s;
    }
}