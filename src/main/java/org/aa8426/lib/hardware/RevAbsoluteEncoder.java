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
        //      0.9 offset, 0.5 size (range)
        // 
        //             (0.95 + .1), 0.05
        //             (0.05 + .1), 0.15
        //             (0.35 + .1), 0.45
        //             (0.45 + .1), 0.55 Out of range (but without size won't read that way)
        //             (0.80 + .1), 0.9 Out of range (but without size won't read that way)
        double val;
        val = absEncoder.get() + (1.0 - offset); // -0.05                       
        if (val > 1) {
            val = val - 1.0; 
        }
        if (reverse) {
            val = size - val; // .5 - val .55
        }
        if (val < 0) { // should only happen on reverse with a defined size
            val = 1 + val;
        }
        return val;
    }

    public double getScaledReading() {
        double range  = scaleMax - scaleMin;
        return ((getAdjustedReading() / size) * range) + scaleMin;
    }

    @Override
    public SendableFluent addSendables(SendableFluent s) {                
        if (s.debugMode) {
            s.addDefaultKey("revabsencoder");
            
            s.addDouble("rawReading", () -> this.getRawReading(), null);
            s.addDouble("offset", this::getOffset, null);
            s.addDouble("size", () -> this.size, null);

            s.addBoolean("inverted", () -> reverse, null);
            //s.addDouble("", this::, this::setAnglePerRotation);
            //s.addBoolean("pidEnabled", this::isPidEnabled, null);
            
            s.removeDefaultKey();
        }
        return s;
    }
}