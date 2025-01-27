// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package org.aa8426;

import org.aa8426.examples.odometry.OdometryExample;
import edu.wpi.first.wpilibj.RobotBase;

public final class Main {
  private Main() {}

  public static void main(String... args) {
    //RobotBase.startRobot(Robot::new);
    //RobotBase.startRobot(TestCommandRobot::new);
    //RobotBase.startRobot(LedCommandRobot::new);
    //RobotBase.startRobot(MotorTestRobot::new);
    RobotBase.startRobot(OdometryExample::new);
  }
}
