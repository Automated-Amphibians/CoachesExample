package org.aa8426.subsystems;

import org.aa8426.lib.examples.RobotUtils;

import edu.wpi.first.wpilibj2.command.Subsystem;

public class FakeMotor implements Subsystem {
    
    public FakeMotor() {
        RobotUtils.logUptime("FAKEMOTOR CONSTRUCT");
    }

    public void startMotor() {
        System.out.println("I have started the motor");
    }

    public void stopMotor() {
        System.out.println("I have stopped the motor");
    }

    public void periodic() {
        RobotUtils.logUptime("FAKEMOTOR PERIODIC");
    }
}
