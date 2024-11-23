package frc.robot.examples.commands;

import edu.wpi.first.wpilibj2.command.Command;

public class CmdTemplate extends Command {

    // called before run
    @Override
    public void initialize() {      
    }
  
    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
    }

    // Called when the command is terminated either because it finished or it was interrupted.
    @Override
    public void end(boolean interrupted) {
    }
  
    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
      return false;
    }
  
  
}
