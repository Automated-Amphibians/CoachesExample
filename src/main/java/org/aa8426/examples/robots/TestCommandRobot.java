// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.aa8426.examples.robots;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import org.aa8426.RobotContainer;
import org.aa8426.examples.commands.CmdExampleForSlides1;

public class TestCommandRobot extends TimedRobot {

  private RobotContainer rc;  

  

  @Override
  public void robotInit() {
    rc = new RobotContainer();
  }

  @Override
  public void robotPeriodic() {
    CommandScheduler.getInstance().run();
  }

  @Override
  public void testInit() {    
    //CmdExamples.logScheduler(false);
    
    //InterruptionBehavior ib;
    
    /* 
    Runnable task = () -> {
      System.out.println("Hello world!");
    };
    Command cmd = Commands.run(task);
    cmd.schedule();    
    */

    /***** EXAMPLES FOR COMMANDS *******/

    //CmdExampleForSlides1.printHelloWorld(); 
    // CmdExampleForSlides1.printHelloWorldSimple();
    // CmdExampleForSlides1.printHelloWorldSimpleSequence();
    //CmdExampleForSlides1.printHelloWorldSimpleSequenceRepeating();
    // CmdExampleForSlides1.printHelloWorldSimpleSequenceRepeatingBad();
    //CmdExampleForSlides1.printHelloWorldSimpleSequenceRepeatingFixed();

    /***** EXAMPLES FOR TRIGGERS *******/

     CmdExampleForSlides1.printHelloWorldSimpleSequenceRepeatingStoppable(rc.driverPad);
    // CmdExampleForSlides1.printHelloWorldSimpleSequenceRepeatingStoppableWithStartEnd(rc.driverPad);
    // CmdExampleForSlides1.printHelloWorldSimpleSequenceRepeatingStoppableWithStartEndFixed(rc.driverPad);    
    // CmdExampleForSlides1.printHelloButUhOh(rc.driverPad);

    // (ignore)
    // CmdExampleForSlides1.printHelloBut(rc.driverPad);

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
       
    //rc.leds.setVisionSignal(Color.kRed, false);
    //rc.leds.setIntakeSignal(Color.kGreen, true);    
  }

  @Override
  public void testPeriodic() {

  }

}
