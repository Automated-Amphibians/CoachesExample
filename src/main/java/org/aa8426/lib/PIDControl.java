package org.aa8426.lib;

import java.util.function.Consumer;
import java.util.function.DoubleSupplier;

import org.aa8426.lib.dashboard.SendableFluent;
import org.aa8426.lib.dashboard.SendableFluent.ISendableFluent;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.Subsystem;


/**
 * This wraps the WPI PIDController class with a little bit of extra functionality 
 * 
 */
public class PIDControl implements ISendableFluent {
    private PIDController pid;
    private double min = 0;
    private double max = 1;    
    private double lastPower;
    private SlewRateLimiter slr = null;
    private double maxRampUp = 9999;
    private double maxTarget;
    private double minTarget;
    //public double invert = 1;
    
    /**
     * When using the Command version of PID control, to prevent the constant chatter
     * of updating the duty cycle of the motor on the CAN chassis, this setting will ignore
     * values that were within a certain magnitude of the previous value. Often motors
     * are incapable of tweaking beyond 1%, so 0.01 is a solid value for this field, presuming
     * your output is from 0-1.
     */
    private Double dupeOutputTolerance = null;
    private DoubleSupplier measurementSupplier;
    private Consumer<Double> outputConsumer;
    private double lastMeasurement = 0.0;
    
    public PIDControl(double kP, double kI, double kD, double minTarget, double maxTarget) {
        this.pid = new PIDController(kP, kI, kD/* , period */);   
        this.minTarget = minTarget;
        this.maxTarget = maxTarget;
    }

    public PIDControl(double kP, double kI, double kD) {
        this.pid = new PIDController(kP, kI, kD/* , period */);   
        this.minTarget = Double.MIN_VALUE;
        this.maxTarget = Double.MAX_VALUE;
    }

    public double getMinTarget() { return minTarget; }
    public void setMinTarget(double val) { this.minTarget = val; }
    public double getMaxTarget() { return maxTarget; }
    public void setMaxTarget(double val) { this.maxTarget = val; }
    public double getP() { return this.pid.getP(); }
    public void setP(double p) { this.pid.setP(p); }
    public double getI() { return this.pid.getI(); }
    public void setI(double i) { this.pid.setI(i); }
    public double getD() { return this.pid.getD(); }
    public void setD(double d) { this.pid.setD(d); }        
    public double getMax() { return this.max;}
    public void setMax(double d) { this.max = d;}
    public double getMaxRampUp() { return this.maxRampUp;}
    public void setMaxRampUp(double d) { this.maxRampUp = d; if (this.maxRampUp < 10) {this.slr = new SlewRateLimiter(d);}}
    public double getMin() { return this.min;}
    public void setMin(double d) { this.min = d;}    
    public double getDupeOutputTolerance() { return this.dupeOutputTolerance;}
    public PIDControl setDupeOutputTolerance(double d) { this.dupeOutputTolerance = d; return this;} 

    public PIDControl setMinMax(double min, double max) {
        this.min = min;
        this.max = max;        
        return this;
    }

    public PIDControl enableContinuousInput(double startRange, double endRange) {
        this.pid.enableContinuousInput(startRange, endRange);
        return this;
    }

    public double getTarget() {
        return this.pid.getSetpoint();
    }

    public PIDControl setTarget(double target) {
        if (target > maxTarget) {            
            target = maxTarget;
            //return this;
        }
        if (target < minTarget) {            
            target = minTarget;
            //return this;
        }
        System.out.println("target="+target);
        this.pid.setSetpoint(target);        
        return this;
    }

    public PIDControl incrementTarget(double amountToIncrement) {
        double newTarget = this.pid.getSetpoint() + amountToIncrement;
        if (pid.isContinuousInputEnabled()) {
            while (newTarget > maxTarget) {
                newTarget = newTarget - maxTarget;
            }
            while (newTarget < minTarget) {
                newTarget = newTarget + maxTarget;
            }
        }        
        return this.setTarget(newTarget);        
    }

    public double getTolerance() {
        return this.pid.getErrorTolerance();
    }
    
    public PIDControl setTolerance(double tolerance) {
        this.pid.setTolerance(tolerance);        
        return this;
    }

    public PIDControl setMeasurementAndOutput(DoubleSupplier measurementSupplier, Consumer<Double> outputConsumer) {
        this.measurementSupplier = measurementSupplier;
        this.outputConsumer = outputConsumer;        
        return this;
    }

    public Double calc(double measurement) {        
        double output = pid.calculate(measurement);                
        // if (this.pid.atSetpoint()) {
        //     return 0.0; // or should this be a minimum?
        // }
        
        double newOutput = output < 0 ? MathUtil.clamp(output, -max, -min) : MathUtil.clamp(output, min, max);
        //System.out.println(String.format("output=%.2f, newOutput=%.2f", output, newOutput));
        //output = MathUtil.clamp(output, min, max);
        //log.log("measurement="+measurement+",error="+this.pid.getPositionError()+",target="+this.pid.getSetpoint()+",output="+output+",atsetpoint="+this.pid.atSetpoint());
        return newOutput;
    }    

    public double getLastPower() {
        return this.lastPower;
    }    

    public boolean atSetpoint() {
        return pid.atSetpoint();
    }

    public double calcPower() {
        lastMeasurement = measurementSupplier.getAsDouble();
        return calc(lastMeasurement);
    }

    public void periodic() {        
        double power = calcPower();
        if (dupeOutputTolerance == null || RobotBase.isSimulation()) {
            lastPower = power;
            outputConsumer.accept(power);
            return;
        }
        if (slr != null) {
            power = slr.calculate(power); 
        }
        double powerDiff = Math.abs(power - lastPower);
        if (powerDiff < dupeOutputTolerance) {            
            return;
        }
        outputConsumer.accept(power);
        lastPower = power;        
    }

    public Command pidDefaultCmd(DoubleSupplier measurementSupplier, Consumer<Double> outputConsumer, Subsystem... requirements) {
        setMeasurementAndOutput(measurementSupplier, outputConsumer);        
        return Commands.run(this::periodic, requirements);
    }
    
    public SendableFluent addSendables(SendableFluent s) {        
        s.addDefaultKey("pid");
        s.addDouble("lastPower", this::getLastPower, null);
        //s.addDouble("lastMeasurement", this::getLastMeasurement, null);
        s.addBoolean("atSetpoint", this::atSetpoint, null);
        if (s.debugMode) {
            s.addDouble("p", this::getP, this::setP);
            s.addDouble("i", this::getI, this::setI);
            s.addDouble("d", this::getD, this::setD);
            s.addDouble("minPower", this::getMin, this::setMin);
            s.addDouble("maxPower", this::getMax, this::setMax);
            s.addDouble("target", this::getTarget, this::setTarget);
            s.addDouble("tolerance", this::getTolerance, this::setTolerance);
            s.addDouble("lastMeasure", () -> lastMeasurement, null);
            s.addDouble("calcPower", () -> calcPower(), null);
            //s.addDouble("maxRampUp", this::getMaxRampUp, this::setMaxRampUp);
            //s.addDouble("dupePowerTolerance", this::getDupeOutputTolerance, this::setDupeOutputTolerance);        
        }
        s.removeDefaultKey();
        return s;
    }

    public static void main(String[] args) {
        //System.out.println(MathUtil.clamp(-2, 2, 4));
    }

}
