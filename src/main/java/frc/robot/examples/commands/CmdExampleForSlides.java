package frc.robot.examples.commands;

import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.FakeMotor;

public class CmdExampleForSlides {
        

    // not used anywhere, not meant to be run
    public void doNothing() {
        FakeMotor fakeMotor = new FakeMotor();

        Runnable startMotor = () -> {
            fakeMotor.startMotor();
        };
        Commands.run(startMotor).schedule();
    }

    public void doNothing2() {
        //Commands.repeatingSequence(n
    }
}
