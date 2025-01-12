package org.aa8426.examples.robots;

import org.aa8426.RobotContainer;

import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkBaseConfig;
import com.revrobotics.spark.SparkMax;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;

public class MotorTestRobot extends TimedRobot { 

    private RobotContainer rc;
    //TalonFX motor = new TalonFX(1);
    SparkMax motor = new SparkMax(2, MotorType.kBrushless);

    @Override
    public void robotInit() {
        rc = new RobotContainer();        
        //SparkBaseConfig sparkBaseConfig = new SparkBaseConfig().
        //motor.configure(null, null, null)
        /* 
        TalonFXConfiguration config = new TalonFXConfiguration();                
        motor.getConfigurator().apply(config);
        */

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
        rc.driverPad.y().onTrue(Commands.runOnce(() -> {
            //motor.set(0.015);
            System.out.println("Turned on...");
            motor.set(0.25);
        }));
        rc.driverPad.a().onTrue(Commands.runOnce(() -> {
            System.out.println("Turned off...");
            motor.set(0.0);
        }));        

    }
}
