package org.aa8426.utils;


import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class Utils {
 
    public static boolean amRedAlliance() {
        return Alliance.Red.equals(DriverStation.getAlliance().get());
    }

    public static boolean amBlueAlliance() {
        return Alliance.Blue.equals(DriverStation.getAlliance().get());        
    }
}
