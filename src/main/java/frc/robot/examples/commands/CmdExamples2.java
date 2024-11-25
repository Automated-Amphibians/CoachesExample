package frc.robot.examples.commands;

import java.util.function.BooleanSupplier;

import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

/**
 * I highly recommend reading the below documentation, but this class is just meant to be a quick testing and example guide for 
 * writing commands.
 * 
 * https://docs.wpilib.org/en/stable/docs/software/dashboards/glass/command-based-widgets.html -- allows you to select commands in the shuffleboard
 * https://docs.wpilib.org/en/stable/docs/software/commandbased/index.html
 * 
 * Remember, that these commands will not run unless the robot is enabled in *some* mode. (auton, tele, test)
 * 
 */
public class CmdExamples2 {

    public static Runnable out(String msg) {          
        return () -> {
            System.out.println(String.format("%3.1f", Timer.getFPGATimestamp()) + ":" + msg);
        };
    }

    /** Runs forever when triggered  */
    public static void example1(CommandXboxController ctrl) {     
     ctrl.rightBumper().onTrue(
      Commands.run(out("Hello!"))
     );
    }

    /** Runs just once, not matter how long held -- commands started with "runOnce" ALWAYS run one time */
    public static void example1a(CommandXboxController ctrl) {
     ctrl.rightBumper().whileTrue(
      Commands.runOnce(out("Hello!"))
     );
    }

    /** 
     * Runs a command over and over while a condition holds true.
     * 
     * Good use cases: Aiming the drivetrain at a target as long as it is held.
     */
    public static void example2(CommandXboxController ctrl) {
     ctrl.rightBumper().whileTrue(
      Commands.run(out("Hello!"))
     );
    }

   /** 
     * Runs a command over and over while a condition holds true and then runs a cleanup or finalizing command after the condition is no longer true.
     * 
     * Good use cases: Aiming a mechanism at a target as long as it is held, then reverting to a predefined position when it is released. (for example an arm)
     * 
     */
    public static void example2a(CommandXboxController ctrl) {
     ctrl.rightBumper().whileTrue(
      Commands.runEnd(out("Hello!"), out("Goodbye"))
     );
    }

    /** 
     * Runs a command over and over until the button is pressed again 
     * 
     * Good use cases: Aiming a mechanism at a target when first triggered, then reverting to a predefined position when it is triggered again. (for example an arm)
     * 
     */
    public static void example3(CommandXboxController ctrl) {
        ctrl.rightBumper()
        .toggleOnTrue(Commands.runEnd(out("Toggled to running"), out("Toggled to stopped")))   
        ;
    }    

    /** 
     * Runs a command over and over while two conditions are true (right bumper is being held, but the left bumper hasn't been pressed)
     *     
     * Good use cases: Running an intake while holding a button but stopping when a condition is met such as a sensor being read or the button is released. 
     *       
     */
    public static void example4(CommandXboxController ctrl) {        
        BooleanSupplier conditions = () -> {            
            return !ctrl.getHID().getRightBumper() || (ctrl.getHID().getLeftBumper());
        };
        ctrl.rightBumper().whileTrue(
            //Commands.runEnd(out("Hello!"), out("Goodbye"))
            Commands.runEnd(out("Hello!"), out("Goodbye"))
            .until(conditions).unless(ctrl.getHID()::getLeftBumper)
        );
        
    }

    /**
     * Runs a command only after 3 seconds of holding the button (rising), can be switched to falling to insure ta least 3 seconds of execution
     *      
     * Use cases - you want to make sure a particular action happens for a given amount of time (falling), or that the driver is really sure
     *             they want to do a thing by making them hold it a long time? (rising)
     * 
     * Not particularly handy, but here it is.
     */
    public static void example5(CommandXboxController ctrl) {                
        ctrl.rightBumper().debounce(3, DebounceType.kRising).whileTrue(
            Commands.runEnd(out("Hello!"), out("Goodbye"))
        );
    }


    /**
     * This is really the golden example of why to use commands. Keeping track of the state of each of these commands is painful.
     * 
     * Most of these commands should probably be 
     */
    public static Command example6(CommandXboxController ctrl) {             
            return Commands.sequence(        
                Commands.runOnce(out("Started something - doing nothing while waiting for left bumper")),
                Commands.idle().until(ctrl.getHID()::getLeftBumperPressed),
                Commands.runOnce(out("Do a new thing 1/3")),
                Commands.repeatingSequence(
                    Commands.runOnce(out(" And another 2/3 over and over (waiting for left bumper again)")),
                    Commands.idle().withTimeout(0.5)
                    ).until(ctrl.getHID()::getLeftBumperPressed),
                Commands.runOnce(out("lastly 3/3 (waiting 2 seconds before next)")),
                Commands.idle().withTimeout(2), // this may be needed to let the press clear before below
                Commands.race(
                    Commands.run(out("Beep")).until(ctrl.getHID()::getLeftBumperPressed),
                    Commands.run(out("Boop")).until(ctrl.getHID()::getRightBumperPressed)                    
                ),                
                Commands.runOnce(out("All done!"))
            );        
    }


    

}
