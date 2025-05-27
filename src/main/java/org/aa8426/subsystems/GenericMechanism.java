package org.aa8426.subsystems;

import org.aa8426.lib.PIDControl;
import org.aa8426.lib.dashboard.SendableFluent;
import org.aa8426.lib.dashboard.SendableFluent.ISendableFluent;
import org.aa8426.lib.hardware.RevAbsoluteEncoder;
import org.aa8426.lib.hardware.motors.Motor;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class GenericMechanism extends SubsystemBase implements ISendableFluent {
    
    //private PIDControl pid = new PIDControl(0.005, 0.0, 0.0);
    private PIDControl pid = new PIDControl(0.005, 0.0, 0.0);
    private RevAbsoluteEncoder absoluteEncoder = new RevAbsoluteEncoder(9)
                                                    .setReverse(false)
                                                    .setOffsetAndSize(0.508, 1)
                                                    .setScale(0.0, 360.0);
                                                    
    private Motor motor;
    private Double manualPower = null;
    private boolean disabled = false;
                                                    
    public GenericMechanism(Motor motor) {
        this.motor = motor;        
        // I know I want the motor to move at a max of M duty cycle.
        // I know the motor needs to hold its position with H duty cycle.
        // I want to use a range of B to T
        // I am pretty sure we can use max power up until C distance.        
        this.addSendables(SendableFluent.getInstance()); 
        //pid.setMinMax(0.0125, 0.1);
        pid.setMinMax(0.0, 0.1);        
        pid.setTarget(30);
        pid.setTolerance(2);
        pid.enableContinuousInput(0, 360);
        pid.setMinTarget(0);        
        pid.setMaxTarget(360);        
        this.setDefaultCommand(this.getPidCommand());
    }

    public PIDControl getPID() {
        return this.pid;
    }

    public double getCurrentAngle() {
        return this.absoluteEncoder.getScaledReading();  
    }

    public void receivePowerCalculationFromPID(double power) {
        if (disabled) {
            return;
        }
        if (manualPower != null) {
            motor.set(manualPower);
            System.out.println(manualPower);
            return;
        }
        motor.set(power);                
    }

    public Command getPidCommand() {
        Command pidCmd = pid.pidDefaultCmd(() -> this.getCurrentAngle(), this::receivePowerCalculationFromPID, this);
        return pidCmd;
    }

    public Command stopCmd() {
        return Commands.runOnce(this::stop);
    }

    public void setManualPower(Double power) {
        disabled = false;
        this.manualPower = power;
    }

    public Double getManualPower() {
        return this.manualPower;
    }

    public Double addToManualPower(double manualPowerToAdd) {
        disabled = false;
        if (this.manualPower == null) {
            this.manualPower = manualPowerToAdd;             
        } else {
            this.manualPower += manualPowerToAdd;
        }
        return this.manualPower;
    }
    
    public void stop() {
        disabled = true;
        manualPower = null;
        motor.set(0);
    }    

    @Override
    public SendableFluent addSendables(SendableFluent s) {                
        s.addDefaultKey("arm");
        if (s.debugMode) {            
            s.addDouble("currentAngle", () -> this.getCurrentAngle(), null);            
            motor.addSendables(s);
            pid.addSendables(s);
            absoluteEncoder.addSendables(s);
        }
        s.removeDefaultKey();
        return s;
    }

    public void start() {        
        disabled = false;
    }
}
