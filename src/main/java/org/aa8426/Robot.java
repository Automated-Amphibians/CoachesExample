// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.aa8426;

import edu.wpi.first.wpilibj.TimedRobot;

public class Robot extends TimedRobot {  

  @SuppressWarnings("unused") // lets us ignore this particular 
  private RobotContainer rc;
  private int count = 0;

  public Robot() {
    super(1); // Set the periodic loop rate to 1 second -- this is only done to make this example readable.
  }

  @Override
  public void robotInit() {  
    /**
     * Initialize the robot hardware within RobotContainer.
     * 
     * Why isn't this done in the constructor (hint: is the underlying robot hardware ready)
     */
    rc = new RobotContainer();    
  }

  @Override
  public void robotPeriodic() {
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
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void autonomousExit() {
  }

  @Override
  public void teleopInit() {  
  }

  @Override
  public void teleopPeriodic() {    
    count = count + 1;
    if (count > 20) {
      Thread.dumpStack();      
      count = 0;
    }
  }

  @Override
  public void teleopExit() {    
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
