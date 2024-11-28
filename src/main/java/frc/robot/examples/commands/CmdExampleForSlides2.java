package frc.robot.examples.commands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

public class CmdExampleForSlides2 {

    static public class PizzaBox {

        public Command startShooterMotorCmd(double targetSpeed) { return null; }
        public Command aimWrist(double targetWristAngle) { return null; }            
        public Command waitForWristAndShooterToBeReady() { return null; }
        public Command shoot() { return null; }
        public Command stopIndexerAndShooter() { return null; }
        public Command lowerWrist() { return null; }

    }
    static public class Leds {
        public Command signalIntakeClear() { return null; }
    }

    static PizzaBox pizzaBox = new PizzaBox();
    static Leds leds = new Leds();

    static public Command getAimAndShootCmd(double targetSpeed, double targetWristAngle) {
        return Commands.sequence(
             pizzaBox.startShooterMotorCmd(targetSpeed),
             pizzaBox.aimWrist(targetWristAngle),
             pizzaBox.waitForWristAndShooterToBeReady(),
             pizzaBox.shoot(),
             pizzaBox.stopIndexerAndShooter(),
             pizzaBox.lowerWrist(),
             leds.signalIntakeClear()
        );        
    }

    static public Command test() {
        return new CmdClassExample();

    }

    // write a command 
}
