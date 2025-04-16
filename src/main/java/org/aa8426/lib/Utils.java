package org.aa8426.lib;

import java.util.Optional;
import edu.wpi.first.math.Pair;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class Utils {

    static public class Triple<A, B, C> {
        private final A first;
        private final B second;
        private final C third;
    
        public Triple(A first, B second, C third) {
            this.first = first;
            this.second = second;
            this.third = third;
        }

        static public <A, B, C> Triple<A, B, C> of(A first, B second, C third) {
            return new Triple<A, B, C>(first, second, third);
        }
    
        public A getFirst() { return first; }
        public B getSecond() { return second; }
        public C getThird() { return third; }
    
        @Override
        public String toString() {
            return "(" + first + ", " + second + ", " + third + ")";
        }
    }
    
    public static String getRobotId() {
        System.out.println("Robot Serial Number: "+RobotController.getSerialNumber());
        if(RobotBase.isSimulation())
            return "neo";
        else if("0327B986".equals(RobotController.getSerialNumber()))
            return "sonic";
        else if("0318860e".equals(RobotController.getSerialNumber())) {
            return "neo"; // test bench
        } 
        else if ("0327B986".equals(RobotController.getSerialNumber())) {
            return "sonic";
        } 
        else if ("034159C7".equals(RobotController.getSerialNumber())) {
            return "waverunner";
        }
        else {
            return "ruby";
        }
    }

    /** If for some reason, alliance has not been initialized, we assume we are on the red side */
    static public boolean amRedAlliance() {
        Optional<Alliance> alliance = DriverStation.getAlliance();
        if (alliance.isEmpty()) {
            return true;
        }
        return Alliance.Red.equals(DriverStation.getAlliance().get());
    }

    /** If for some reason, alliance has not been initialized, we assume we are on the red side */
    static public boolean amBlueAlliance() {
        Optional<Alliance> alliance = DriverStation.getAlliance();
        if (alliance.isEmpty()) {
            return false;
        }
        return Alliance.Blue.equals(DriverStation.getAlliance().get());
    }

    static public String formatDouble(double val, int digits_to_show)  {
        return String.format("%."+digits_to_show+"f", val);
    }

    static public Pair<Rotation2d, Double> angleAndDistance(double x1, double y1, double x2, double y2) {        
        return Pair.of(
            Rotation2d.fromRadians(Math.atan2(y2 - y1, x2 - x1)),
            Math.hypot(x2 - x1, y2 - y1)            
        );
    }

    /** Frog stuff */
    public static Translation2d cleanseSpeeds(Translation2d movement) {
        return movement.getNorm() < 0.05 ? new Translation2d() : movement;
    }
    
}
