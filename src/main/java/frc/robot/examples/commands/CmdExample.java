package frc.robot.examples.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;

public class CmdExample extends Command {
    double startTime;

    // called before run
    @Override
    public void initialize() {      
      startTime = Timer.getFPGATimestamp();
      System.out.println("Initializing Command...");
    }
  
    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
      System.out.println("Executing at: "+Timer.getFPGATimestamp());
    }

    // Called when the command is terminated either because it finished or it was interrupted.
    @Override
    public void end(boolean interrupted) {
      System.out.println("End...");
    }
  
    // Returns true when the command should end.
    @Override
    public boolean isFinished() {      
      if ((Timer.getFPGATimestamp() - this.startTime) > 0.5) {
        return true;
      }
      return false;
    }

}

