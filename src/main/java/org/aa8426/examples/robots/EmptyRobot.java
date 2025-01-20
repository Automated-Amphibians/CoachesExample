package org.aa8426.examples.robots;

import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;

public class EmptyRobot extends TimedRobot{
    

    @Override
    public void robotInit() {
        System.out.println("Serial Number:"+RobotController.getSerialNumber());
    }

    @Override
    public void disabledPeriodic() {

    }

    @Override
    public void robotPeriodic() {
    }
}
