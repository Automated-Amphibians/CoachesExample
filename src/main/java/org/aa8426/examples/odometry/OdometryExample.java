package org.aa8426.examples.odometry;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class OdometryExample extends TimedRobot {
    
    
    @Override
    public void robotInit() {        
        
    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
    }

    @Override
    public void teleopInit() {

        
    }

    @Override
    public void teleopPeriodic() {        
        
    }

}
