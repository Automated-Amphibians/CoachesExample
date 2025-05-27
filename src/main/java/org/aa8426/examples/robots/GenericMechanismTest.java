package org.aa8426.examples.robots;

import java.util.function.BooleanSupplier;

import org.aa8426.RobotContainer;
import org.aa8426.lib.dashboard.SendableFluent;
import org.aa8426.lib.hardware.motors.Motor;
import org.aa8426.lib.hardware.motors.Motor.MotorTypeName;
import org.aa8426.subsystems.GenericMechanism;

import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class GenericMechanismTest extends TimedRobot { 

    GenericMechanism gmt;
    RobotContainer rc;

    @Override
    public void robotInit() {
        rc = new RobotContainer();        
        gmt = new GenericMechanism(
            Motor.create(MotorTypeName.TALON_KRAKEN, 1, 40, 40, 7)            
        );
        //this.addSendables(SendableFluent.getInstance());
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
        Command addPower = Commands.runOnce(() -> {            
            double newPower = gmt.addToManualPower(0.01);
            System.out.println("+ newPower="+newPower);
        });

        Command lowerPower = Commands.runOnce(() -> {
            double newPower = gmt.addToManualPower(-0.01);
            System.out.println("- newPower="+newPower);
        });

        Command powerOff = Commands.runOnce(() -> {
            System.out.println("Turned off...");
            gmt.stop();
        });

        Command powerOn = Commands.runOnce(() -> {
            System.out.println("Turned on...");
            gmt.start();
        });

        Command bumpTargetUp = Commands.run(() -> {
            System.out.println("Increment target up...");
            gmt.getPID().incrementTarget(1);
        });

        Command bumpTargetDown = Commands.run(() -> {
            System.out.println("Decrement target up...");
            gmt.getPID().incrementTarget(-1);
        });

        
        CommandScheduler.getInstance().getDefaultButtonLoop().clear();

        rc.driverPad.pov(0).onTrue(addPower);
        rc.driverPad.pov(180).onTrue(lowerPower);

        rc.driverPad.pov(90).whileTrue(bumpTargetDown);
        rc.driverPad.pov(270).whileTrue(bumpTargetUp);

        BooleanSupplier anyBumperPressed = 
            () -> 
                rc.driverPad.getHID().getLeftBumperButtonPressed() || 
                rc.driverPad.getHID().getRightBumperButtonPressed();

        new Trigger(anyBumperPressed).onTrue(powerOff);

        rc.driverPad.start().onTrue(powerOn);

        rc.driverPad.y().onTrue(Commands.runOnce(() -> gmt.getPID().setTarget(0)));
        rc.driverPad.x().onTrue(Commands.runOnce(() -> gmt.getPID().setTarget(81)));
        rc.driverPad.b().onTrue(Commands.runOnce(() -> gmt.getPID().setTarget(278)));
        rc.driverPad.a().onTrue(Commands.runOnce(() -> gmt.getPID().setTarget(180)));
    }    
    
}
