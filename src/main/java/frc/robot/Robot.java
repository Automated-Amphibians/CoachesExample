// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.examples.commands.CmdExampleForSlides1;
import frc.robot.examples.commands.CmdExampleForSlides2;
import frc.robot.examples.commands.CmdExamples;

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private RobotContainer m_robotContainer;
  private CommandXboxController m_gamepad = new CommandXboxController(0);
  

  @Override
  public void robotInit() {
    m_robotContainer = new RobotContainer();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void disabledInit() {

  }

  @Override
  public void disabledPeriodic() {

  }

  @Override
  public void disabledExit() {

  }

  @Override
  public void autonomousInit() {
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  @Override
  public void autonomousPeriodic() {

  }

  @Override
  public void autonomousExit() {
    
  }

  @Override
  public void teleopInit() {
    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  @Override
  public void teleopPeriodic() {

  }

  @Override
  public void teleopExit() {

  }

  public void printHello() {
    System.out.println("Hello!");
  }

  public boolean getABool() {
    // pretend something interesting happens here and returns false or true based on that
    return false;
  }



  @Override
  public void testInit() {    
    //CmdExamples.logScheduler(false);
    CommandScheduler.getInstance().cancelAll();
    CommandScheduler.getInstance().getDefaultButtonLoop().clear();
    
    /*
     Runnable task = () -> {
      System.out.println("Hello world!");
    };
    Command cmd = Commands.run(task);
    cmd.schedule();    
    */

    /***** EXAMPLES FOR COMMANDS *******/

    // CmdExampleForSlides1.printHelloWorld(); 
    // CmdExampleForSlides1.printHelloWorldSimple();
    // CmdExampleForSlides1.printHelloWorldSimpleSequence();
    // CmdExampleForSlides1.printHelloWorldSimpleSequenceRepeating();
    // CmdExampleForSlides1.printHelloWorldSimpleSequenceRepeatingBad();
    // CmdExampleForSlides1.printHelloWorldSimpleSequenceRepeatingFixed();

    /***** EXAMPLES FOR TRIGGERS *******/

    // CmdExampleForSlides1.printHelloWorldSimpleSequenceRepeatingStoppable(m_gamepad);
    // CmdExampleForSlides1.printHelloWorldSimpleSequenceRepeatingStoppableWithStartEnd(m_gamepad);
    // CmdExampleForSlides1.printHelloWorldSimpleSequenceRepeatingStoppableWithStartEndFixed(m_gamepad);    
    // CmdExampleForSlides1.printHelloButUhOh(m_gamepad);

    // (ignore)
     CmdExampleForSlides1.printHelloBut(m_gamepad);

    /***** Examples with explanations ********/
    // CmdExamples.example1(m_controller);  // on click turn on forever
    // CmdExamples.example1a(m_controller); // on click run once
    // CmdExamples.example1b(m_controller); // run once when clicked, with an end command
    // CmdExamples.example2(m_controller);  // run as long as held
    // CmdExamples.example2a(m_controller); // run as long as held, with an end command    
    // CmdExamples.example3(m_controller);  // run continiously on a single click, then stop on second click (toggle on off)
    // CmdExamples.example4(m_controller);
    // CmdExamples.example5(m_controller);
    // CmdExamples.example6(m_controller);
        
  }

  @Override
  public void testPeriodic() {
    
  }

  @Override
  public void testExit() {

  }
}
