package frc.robot.examples.commands;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import static edu.wpi.first.wpilibj2.command.Commands.print;
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
public class CmdExamples {    

    /** 
     * Run this prior to scheduling the examples if you want to see what the scheduler is doing. 
     * 
     **/
    static public void logScheduler(boolean logExecution) {
        CommandScheduler.getInstance().onCommandInitialize(getEventHandler("Init Command:"));
        CommandScheduler.getInstance().onCommandFinish(getEventHandler("Finish Command:"));
        if (logExecution) {
            CommandScheduler.getInstance().onCommandExecute(getEventHandler("Exec Command:"));
        }
    }

    /** Runs forever when triggered  */
    public static void example1(XboxController ctrl) {     
     new Trigger(ctrl::getRightBumper).onTrue(
      Commands.run(out("Hello!"))
     );     
    }

    /** Runs just once, not matter how long held -- commands started with "runOnce" ALWAYS run one time */
    public static void example1a(XboxController ctrl) {
     new Trigger(ctrl::getRightBumper).whileTrue(
      Commands.runOnce(out("Hello!"))
     );
    }

    /** 
     * Runs a command just once, then when the key is released, runs a cleanup command 
     *
     * Uses - start and stopping up a motor without issuing the command over and over and over.
     *  
     ***/
    public static void example1b(XboxController ctrl) {     
        /*
     new Trigger(ctrl::getRightBumper).whileTrue(
      Commands.startEnd(out("Started"), out("ended"))
     );*/

     // command equivalent
     new Trigger(ctrl::getRightBumper)
        .onTrue(print("hello!"))
        .onFalse(print("goodbye"));
    }

    /** 
     * Runs a command over and over while a condition holds true.
     * 
     * Good use cases: Aiming the drivetrain at a target as long as it is held.
     */
    public static void example2(XboxController ctrl) {
     new Trigger(ctrl::getRightBumper).whileTrue(
      Commands.run(out("Hello!"))
     );
    }

   /** 
     * Runs a command over and over while a condition holds true and then runs a cleanup or finalizing command after the condition is no longer true.
     * 
     * Good use cases: Aiming a mechanism at a target as long as it is held, then reverting to a predefined position when it is released. (for example an arm)
     * 
     */
    public static void example2a(XboxController ctrl) {
     new Trigger(ctrl::getRightBumper).whileTrue(
      Commands.runEnd(out("Hello!"), out("Goodbye"))
     ).onFalse(print("extra goodbye"));
     
    }

    /** 
     * Runs a command over and over until the button is pressed again 
     * 
     * Good use cases: Aiming a mechanism at a target when first triggered, then reverting to a predefined position when it is triggered again. (for example an arm)
     * 
     */
    public static void example3(XboxController ctrl) {
        new Trigger(ctrl::getRightBumperPressed)
        .toggleOnTrue(Commands.runEnd(out("Toggled to running"), out("Toggled to stopped")))   
        ;
    }    

    /** 
     * Runs a command over and over while two conditions are true (right bumper is being held, but the left bumper hasn't been pressed)
     *     
     * Good use cases: Running an intake while holding a button but stopping when a condition is met such as a sensor being read or the button is released. 
     *       
     */
    public static void example4(XboxController ctrl) {        
        BooleanSupplier conditions = () -> {
            return !ctrl.getRightBumper() || (ctrl.getLeftBumper());
        };
        new Trigger(ctrl::getRightBumper).whileTrue(
            //Commands.runEnd(out("Hello!"), out("Goodbye"))
            Commands.runEnd(out("Hello!"), out("Goodbye"))
            .until(conditions).unless(ctrl::getLeftBumper)
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
    public static void example5(XboxController ctrl) {                
        new Trigger(ctrl::getRightBumper).debounce(3, DebounceType.kRising).whileTrue(
            Commands.runEnd(out("Hello!"), out("Goodbye"))
        );
    }


    /**
     * This is really the golden example of why to use commands. Keeping track of the state of each of these commands is painful.
     * 
     * Most of these commands should probably be 
     */
    public static void example6(XboxController ctrl) {             
            Commands.sequence(        
                Commands.runOnce(out("Started something - doing nothing while waiting for left bumper")),
                Commands.idle().until(ctrl::getLeftBumperPressed),
                Commands.runOnce(out("Do a new thing 1/3")),
                Commands.repeatingSequence(
                    Commands.runOnce(out(" And another 2/3 over and over (waiting for left bumper again)")),
                    Commands.idle().withTimeout(0.5)
                    ).until(ctrl::getLeftBumperPressed),
                Commands.runOnce(out("lastly 3/3 (waiting 2 seconds before next)")),
                Commands.idle().withTimeout(2), // this may be needed to let the press clear before below
                Commands.race(
                    Commands.run(out("Beep")).until(ctrl::getLeftBumperPressed),
                    Commands.run(out("Boop")).until(ctrl::getRightBumperPressed)                    
                ),                
                Commands.runOnce(out("All done!"))
            ).schedule();
        
    }

    // SUPPORT FOR EXAMPLES
    /* Commands.print() exists, but we're trying to demonstrate what happens with 
       runnables.
    */
    static private Runnable out(String msg) {        
        return () -> {
            System.out.println(String.format("%3.1f", Timer.getFPGATimestamp()) + ":" + msg);
        };        
    }

    static private Consumer<Command> getEventHandler(String prefix) {
        return (cmd) -> {
        System.out.println(prefix+cmd.getName());
        };
    }


}
