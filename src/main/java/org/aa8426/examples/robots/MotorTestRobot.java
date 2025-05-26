package org.aa8426.examples.robots;

import org.aa8426.RobotContainer;
import org.aa8426.lib.dashboard.SendableFluent;
import org.aa8426.lib.dashboard.SendableFluent.ISendableFluent;
import org.aa8426.lib.hardware.motors.Motor;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;

public class MotorTestRobot extends TimedRobot implements ISendableFluent { 

    Motor motor = Motor.create("NEO", 2, 40, 40, 1);
    double allowedAccelOfAccel = 0.05; // constant
    double allowedAccel = 0.1; // 
    //double 
    double powerGoal = 0;
    RobotContainer rc;

    @Override
    public void robotInit() {
        rc = new RobotContainer();        
        this.addSendables(SendableFluent.getInstance());
        SendableFluent.getInstance().get("TEST");
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
        CommandScheduler.getInstance().getDefaultButtonLoop().clear();
        rc.driverPad.x().onTrue(Commands.runOnce(() -> {
            System.out.println("Turned on...");
            powerGoal = 1;
        }));
        rc.driverPad.b().onTrue(Commands.runOnce(() -> {
            //motor.set(0.015);
            System.out.println("Turned on...");
            powerGoal = -1;
        }));
        rc.driverPad.a().onTrue(Commands.runOnce(() -> {
            System.out.println("Turned off...");
            powerGoal = 0;
            //slr.reset(0);
            //motor.set(0.0);
        }));        

    }

    @Override
    public void testPeriodic() {
        double actualPower = 0.0;//slr.calculate(powerGoal);        
        System.out.println(actualPower);
        motor.set(actualPower);
    }

    @Override
    public SendableFluent addSendables(SendableFluent s) {
        s.addDefaultKey("test");
        motor.addSendables(s);
        s.removeDefaultKey();
        return s;
    }
}
