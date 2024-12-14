// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.aa8426;

import org.aa8426.subsystems.FakeMotor;
import org.aa8426.subsystems.LedSubsystem;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class RobotContainer {
  public FakeMotor fakeMotor = new FakeMotor();
  public LedSubsystem leds = new LedSubsystem();

  public RobotContainer() {
    configureBindings();
  }

  private void configureBindings() {
    
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
