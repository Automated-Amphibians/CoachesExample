// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.aa8426;

import org.aa8426.lib.examples.RobotUtils;

import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {  

  private RobotContainer rc;

  public Robot() {
    super(2.5); // Set the periodic loop rate to 1 second -- this is only done to make this example readable.
  }

  @Override
  public void robotInit() {  
    rc = new RobotContainer();    
    RobotUtils.logUptime("ROBOT INIT");
  }

  @Override
  public void robotPeriodic() {
    RobotUtils.logUptime("ROBOT PERIODIC");
  }

  @Override
  public void disabledInit() {
    RobotUtils.logUptime("DISABLED INIT");
  }

  @Override
  public void disabledPeriodic() {
    RobotUtils.logUptime("DISABLED PERIODIC");
  }

  @Override
  public void disabledExit() { 
    RobotUtils.logUptime("DISABLED EXIT");
  }

  @Override
  public void autonomousInit() {
    RobotUtils.logUptime("AUTONOMOUS INIT");
  }

  @Override
  public void autonomousPeriodic() {
    RobotUtils.logUptime("AUTONOMOUS PERIODIC");
  }

  @Override
  public void autonomousExit() {
    RobotUtils.logUptime("AUTONOMOUS EXIT");
  }

  @Override
  public void teleopInit() {
    RobotUtils.logUptime("TELEOP INIT");
  }

  @Override
  public void teleopPeriodic() {
    RobotUtils.logUptime("TELEOP PERIODIC");    
    RobotUtils.logMatchtime(" matchtime");    
  }

  @Override
  public void teleopExit() {
    RobotUtils.logUptime("TELEOP EXIT");
  }

  @Override
  public void testInit() {}

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}

  @Override
  public void simulationPeriodic() {}

}
