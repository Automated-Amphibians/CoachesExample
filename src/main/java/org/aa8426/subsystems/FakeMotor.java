package org.aa8426.subsystems;

import org.aa8426.lib.RobotUtils;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.Subsystem;

public class FakeMotor implements Subsystem {
    
    public FakeMotor() {
        RobotUtils.logUptime("FAKEMOTOR CONSTRUCTOR");
        // CommandScheduler.getInstance().registerSubsystem(this);        
        // read the documentation on the Subsystem constructor and Subssytem.periodic!
    }

    public Command startMotorCmd(int pretendSpeed) {
        return Commands.run(() -> {
            startMotor(pretendSpeed);
        });
    }

    public void startMotor(int pretendSpeed) {
        System.out.println("I have started the motor at " + pretendSpeed);
    }

    public void stopMotor() {
        System.out.println("I have stopped the motor");
    }

    @Override
    public void periodic() {
        RobotUtils.logUptime("FAKEMOTOR PERIODIC");
    }
}
