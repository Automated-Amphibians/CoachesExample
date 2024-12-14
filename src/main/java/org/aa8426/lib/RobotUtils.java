package org.aa8426.lib.examples;

import edu.wpi.first.wpilibj.Timer;

public class RobotUtils {
    
  static public void logUptime(String msg) {
    System.out.println(Timer.getFPGATimestamp() + " - " + msg);
  }

  static public void logMatchtime(String msg) {
    System.out.println(Timer.getMatchTime() + " - " + msg);
  }
  
}
