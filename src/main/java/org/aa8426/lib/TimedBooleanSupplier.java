package org.aa8426.lib;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.Timer;

public class TimedBooleanSupplier implements BooleanSupplier{

    private BooleanSupplier condition;
    private BooleanSupplier immediateCondition = null;
    private double time;
    private Timer timer;

    public TimedBooleanSupplier(BooleanSupplier condition, double time) {
        this.condition = condition;
        this.time = time;
        this.timer = new Timer();
    }

    public TimedBooleanSupplier addImmediateEnd(BooleanSupplier condition) {
        this.immediateCondition = condition;
        return this;
    }

    @Override
    public boolean getAsBoolean() {
        if ((immediateCondition != null) && immediateCondition.getAsBoolean()) {
            return true;
        }
        if (this.condition.getAsBoolean()) {            
            if (this.timer.isRunning()) {
                //SmartDashboard.put(timer.
                return (timer.hasElapsed(time));                    
            } else {
                if (time > 0.0) {
                    timer.restart();
                    return false;
                } else {
                    return true;
                }                
            }
        } else {
            timer.stop();
            return false;
        }
    }
    
}
