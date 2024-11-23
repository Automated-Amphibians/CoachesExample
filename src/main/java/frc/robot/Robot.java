// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.concurrent.RunnableScheduledFuture;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.examples.commands.CmdExample;

public class Robot extends TimedRobot {
  private Command m_autonomousCommand;

  private RobotContainer m_robotContainer;
  private XboxController m_controller = new XboxController(0);

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
    CommandScheduler.getInstance().cancelAll();

    int x = 1;

    String myName = "Chris";

    Runnable r1 = this::printHello; 

    Runnable r2 = () -> {
      System.out.println("Hello!");
    };    
    
    BooleanSupplier bs2 = this::getABool;
    BooleanSupplier bs1 = () -> {return false;};
    BooleanSupplier bs3 = () -> false;

    BooleanSupplier bs4 = () -> this.getABool(); // WRONG!
        
    DoubleSupplier ds = () -> {return 3.0;};

    CommandScheduler.getInstance().schedule(new CmdExample());
  }

  @Override
  public void testPeriodic() {

  }

  @Override
  public void testExit() {

  }
}
