package frc.robot.examples.commands;

import java.util.concurrent.atomic.AtomicReference;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;

/**
 * This cmd class is here to demonstrate the different lifecycles of a Command.
 * 
 * I've also provided the Functional equivalent, with the understanding that this sort of breaks the idea
 * of functional programming in that we have a state we are tracking, but is a useful example of another
 * way to create something similar (hopefully provided there is no need for state)
 * 
 */
public class CmdClassExample extends Command {
    private double startTime;

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
      System.out.print("Checking if finished... ");
      if ((Timer.getFPGATimestamp() - this.startTime) > 0.5) {
        return true;
      }
      return false;
    }

    static public Command getSameThing() {
      /* ahhhh, a good opportunity to discuss 
         1. how/why you can't wrap a non-final java variable?
         2. why all programmers should be careful any/all variables in any type of "re-entrant" code

         Ask an LLM: Why can't you wrap non-final variables into a java lambda?
      
         This may not be helpful! The short answer is, anytime state exists
         within re-entrant code, extreme care must be taken to prevent
         race conditions or other unexpected situations that can introduce
         buggy behaviors.

         Lamdas are considered re-entrant by defition in java, and any 
         wrapped locals are stateful and the compiler will attempt to 
         safeguard you from yourself by forcing you to utilize the java
         functional classes, which has methods to mitigate inconsistent
         values being read from the state due to concurrent modification.

         These safeguards are only a first line of defense. If you introduce
         a stateful form, it is up to you to know where the re-entrant code
         may cause issues within your code.

         From a high school student programming a WPILib robot: It is *BAD* idea
         to introduce a stateful variable using Atomics when using the 
         FunctionalCommand. It is perfectly fine to add a final, unmodifiable value
         as an "argument" for use within a FunctionalCommand (or even just a lambda)
         
      */
      final AtomicReference<Double> startTime = new AtomicReference<>(0.0);
      final double value = Math.random();
      return new FunctionalCommand(
        () -> {
          startTime.set(Timer.getFPGATimestamp());
          System.out.println("Initializing Command..." + value);
        }, 
        () -> {
          System.out.println("Executing at: "+Timer.getFPGATimestamp());
        },
        (wasTerminated) -> {
          System.out.println("End...");      
        },
        () -> {
          System.out.print("Checking if finished... ");
          if ((Timer.getFPGATimestamp() - startTime.get()) > 0.5) {
            return true;
          }
          return false;
        }
      );
    }

}

