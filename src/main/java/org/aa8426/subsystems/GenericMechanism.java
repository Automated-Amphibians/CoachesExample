package org.aa8426.subsystems;

import org.aa8426.lib.PIDControl;
import org.aa8426.lib.dashboard.SendableFluent;
import org.aa8426.lib.dashboard.SendableFluent.ISendableFluent;
import org.aa8426.lib.hardware.RevAbsoluteEncoder;
import org.aa8426.lib.hardware.motors.Motor;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class GenericMechanism extends SubsystemBase implements ISendableFluent {
    
    private PIDControl pid = new PIDControl(0.005, 0.0, 0.0);
    private RevAbsoluteEncoder absoluteEncoder = new RevAbsoluteEncoder(9)
                                                    .setReverse(false)
                                                    .setOffsetAndSize(0.508, 1)
                                                    .setScale(0.0, 360.0);
                                                    
    private Motor motor;
    public Double manualPower = null;
                                                    
    public GenericMechanism(Motor motor) {
        this.motor = motor;        
        this.addSendables(SendableFluent.getInstance()); 
        pid.setMinMax(0.01, 0.05);
        pid.setTarget(30);
        pid.setTolerance(5);
        pid.enableContinuousInput(0, 360);
        this.setDefaultCommand(this.getPidCommand());
    }

    public PIDControl getPID() {
        return this.pid;
    }

    public double getCurrentAngle() {
        return this.absoluteEncoder.getScaledReading();  
    }

    public void receivePowerCalculationFromPID(double power) {
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
}
