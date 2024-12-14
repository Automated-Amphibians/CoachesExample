package frc.robot.examples.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

public class CmdExampleForSlides1 {

    static public void printHelloWorld() {
        // Create the code, assign it to a Runnable class
        Runnable task = () -> {
            System.out.println("Hello world!");
        };
        // Create a cmd
        Command cmd = Commands.run(task);
        cmd.schedule();
    }

    static public void printHelloWorldSimple() {        
        Commands.print("Hello world!").schedule();
    }

    static public void printHelloWorldSimpleSequence() {        
        Commands.sequence(
          Commands.print("Hello"),
          Commands.print("World")
        ).schedule();        
    }

    static public void printHelloWorldSimpleSequenceRepeating() {        
        Commands.repeatingSequence(
          Commands.print("Hello"),
          Commands.waitSeconds(1),
          Commands.print("World"),
          Commands.waitSeconds(1)
        ).schedule();        
    }

    static public void printHelloWorldSimpleSequenceRepeatingBad() {        
        Commands.repeatingSequence(
          Commands.run(() -> System.out.println("Hello")),
          Commands.run(() -> System.out.println("World"))
        ).schedule();        
    }

    static public void printHelloWorldSimpleSequenceRepeatingFixed() {        
        Commands.repeatingSequence(
          Commands.runOnce(() -> System.out.println("Hello")),
          Commands.runOnce(() -> System.out.println("World"))
        ).schedule();        
    }

    static public void printHelloWorldSimpleSequenceRepeatingStoppable(CommandXboxController commandXboxController) {        
        commandXboxController.a().whileTrue(
            Commands.repeatingSequence(
              Commands.print("Hello"),
              Commands.print("World")
            )
        );
    }

    static public void printHelloWorldSimpleSequenceRepeatingStoppableWithStartEnd(CommandXboxController commandXboxController) {        
        commandXboxController.a()
          .whileTrue(
            Commands.repeatingSequence(
              Commands.print("Hello"),
              Commands.print("World")
            )
          )
          .onFalse(Commands.print("Goodbye!"))
          .onTrue(Commands.print("Welcome!"));
    }

    static public void printHelloWorldSimpleSequenceRepeatingStoppableWithStartEndFixed(CommandXboxController commandXboxController) {        
        commandXboxController.a()
          .onTrue(Commands.print("Welcome!"))
          .whileTrue(
            Commands.repeatingSequence(
              Commands.print("Hello"),
              Commands.print("World")
            )
          )
          .onFalse(Commands.print("Goodbye!"));
    }

    static public void printHelloButUhOh(CommandXboxController commandXboxController) {        
        Command printHello = Commands.print("Hello!");

        commandXboxController.a()          
          .whileTrue(
            Commands.repeatingSequence(printHello)
          );
        commandXboxController.b()          
          .whileTrue(
            Commands.repeatingSequence(printHello)
          );
    }

    // 
    static public void printHelloBut(CommandXboxController commandXboxController) {                
        commandXboxController.a()
          .onTrue(Commands.print("Welcome!"))
          .whileTrue(
            Commands.repeatingSequence(
              Commands.print("Hello"),
              Commands.waitSeconds(1), 
              Commands.print("World"),
              Commands.waitSeconds(1) 
            )
            .finallyDo(() -> {               
              Commands.print("But wait!").schedule(); // <-- works
              //Commands.print("finally done"); // <-- we are within a runnable! not returning to a function that handles commands
              //System.out.println("Doh!"); // <-- would also work 
            })
            .withTimeout(3) 
          )
          .onFalse(Commands.print("Goodbye!"));
    }


}
