package org.aa8426.subsystems;

import org.aa8426.lib.PIDControl;
import org.aa8426.lib.dashboard.SendableFluent;
import org.aa8426.lib.dashboard.SendableFluent.ISendableFluent;
import org.aa8426.lib.hardware.RevAbsoluteEncoder;
import org.aa8426.lib.hardware.motors.Motor;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import org.aa8426.lib.hardware.IGenericMechanism;

public class GenericMechanism extends SubsystemBase implements ISendableFluent, IGenericMechanism {
    
    //private PIDControl pid = new PIDControl(0.005, 0.0, 0.0);
    private PIDControl pid = new PIDControl(0.008333333333, 0.0, 0.0);
    private RevAbsoluteEncoder absoluteEncoder = new RevAbsoluteEncoder(9)
                                                    .setReverse(false)
                                                    .setOffsetAndSize(0.508, 1)
                                                    .setScale(0.0, 360.0);
                                                    
    private Motor motor;
    private Double manualPower = null;
    private boolean disabled = false;
                                                    
    public GenericMechanism(Motor motor) {
        this.motor = motor;        
        // I know I want the motor to move at a max of M duty cycle. (0.4)
        // What is the max error there is? (What is the maximum distance we can be at any time? 180 degrees)
        // therefore, 180*x = 0.4, x = 0.00222(repeating)
        // It is nice to get a mini graph of significant points.
        // 0.4 at 180 = 
        // 0.2 at 90 degrees off
        // 0.1 at 45
        // 0.05 at 22.5
        // 0.025 at 11.25
        // 0.0125 at 5.6
        
        

        

        /**
         * It is important to understand max motor amounts. 
         * 
         * https://v6.docs.ctr-electronics.com/en/stable/docs/hardware-reference/talonfx/improving-performance-with-current-limits.html
         * https://www.revrobotics.com/neo-550-brushless-motor-locked-rotor-testing/
         * https://www.chiefdelphi.com/t/psa-your-motor-curves-are-wrong-a-whitepaper-about-current-limits/477010
         * 
         * 
         */

        // I know the motor needs to hold its position with H duty cycle. 
        // I want to use a range of B to T
        // I am pretty sure we can use max power up until C distance.        
        this.addSendables(SendableFluent.getInstance()); 
        //pid.setMinMax(0.0125, 0.1);

        pid.setMinMax(0.0, 0.4);        
        pid.setMinTarget(0);        
        pid.setMaxTarget(360);
        pid.setTolerance(2);
        pid.enableContinuousInput(0, 360);        
        //pid.setTarget(30);
        disabled = true;
        this.setDefaultCommand(this.getPidCommand());
    }    

    @Override
    public double getCurrentPosition() {
        return this.absoluteEncoder.getScaledReading();  
    }

    private void receivePowerCalculationFromPID(double power) {
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

    private Command getPidCommand() {
        Command pidCmd = pid.pidDefaultCmd(() -> this.getCurrentPosition(), this::receivePowerCalculationFromPID, this);
        return pidCmd;
    }
    
    public Command stopCmd() {
        return Commands.runOnce(this::stop);
    }

    @Override
    public void setManualPower(Double power) {
        disabled = false;
        this.manualPower = power;
    }    

    @Override
    public double incrementManualPower(double manualPowerToAdd) {
        disabled = false;
        if (this.manualPower == null) {
            this.manualPower = manualPowerToAdd;             
        } else {
            this.manualPower += manualPowerToAdd;
        }
        return this.manualPower;
    }

    @Override
    public void start() {        
        disabled = false;
    }
    
    @Override
    public void stop() {
        disabled = true;        
        motor.set(0);
    }    

    @Override
    public SendableFluent addSendables(SendableFluent s) {                
        s.addDefaultKey("arm");
        if (s.debugMode) {            
            s.addDouble("currentAngle", () -> this.getCurrentPosition(), null);            
            motor.addSendables(s);
            pid.addSendables(s);
            absoluteEncoder.addSendables(s);
        }
        s.removeDefaultKey();
        return s;
    }

    @Override
    public void setTarget(Double target) {
        if (target == null) {
            disabled = true;
            return;
        }
        this.pid.setTarget(target);
    }

    @Override
    public double getMinTarget() {
        return this.pid.getMinTarget();
    }

    @Override
    public double getMaxTarget() {
        return this.pid.getMaxTarget();
    }

    @Override
    public boolean atTarget() {
        return this.pid.atSetpoint();
    }

    @Override
    public double diffFromTarget() {
        return this.pid.getTarget() - this.getCurrentPosition();
    }

    @Override
    public double incrementTarget(double amount) {
        return this.pid.incrementTarget(amount).getTarget();
    }    
    

}
