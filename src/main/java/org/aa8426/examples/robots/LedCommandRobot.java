// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.aa8426.examples.robots;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;

import org.aa8426.RobotContainer;

public class LedCommandRobot extends TimedRobot {

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
    /**
     * Ordinarily we bind commands in RobotContainer or OperatorInterface/DriverInterface (user interface),
     * but if we do that, then we need too many files for an example.
     */
    rc.driverPad.a().onTrue(rc.leds.ledDisplay.entireLed().setLedStateCmd(Color.kGreen));
    rc.driverPad.b().onTrue(rc.leds.ledDisplay.entireLed().setLedStateCmd(Color.kRed));
    rc.driverPad.x().onTrue(rc.leds.ledDisplay.entireLed().setLedStateCmd(Color.kBlue));
    rc.driverPad.y().onTrue(rc.leds.ledDisplay.entireLed().setLedStateCmd(Color.kOrange));
    Commands.repeatingSequence(
      rc.leds.ledDisplay.entireLed().setLedStateCmd(Color.kOrange),
      Commands.waitSeconds(1),
      rc.leds.ledDisplay.entireLed().setLedStateCmd(Color.kBlack),
      Commands.waitSeconds(1)
    ).schedule();
  }

}
