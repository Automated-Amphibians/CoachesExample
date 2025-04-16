package org.aa8426.lib.vision;

import org.aa8426.lib.dashboard.SendableFluent;
import org.aa8426.lib.dashboard.SendableFluent.ISendableFluent;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Timer;

public class VisionSingleTargetInfo implements ISendableFluent {
    public int bestTargetFidId;
    public double bestTargetDistance;
    public double bestTargetAmbiguity;
    public double bestTargetX;
    public double bestTargetY;
    public double bestTargetZ;
    public double bestTargetYawInDeg;
    public double bestTargetAge;
    public double translateTargetX = 0;
    public double translateTargetY = 0;
    public Pose3d lastEstGlobalPose = null;
    public String lastReason = "";
    
    public VisionSingleTargetInfo() {
        clear();        
    }

    public Translation2d getAdjustedTarget() {
        return new Translation2d(bestTargetX + translateTargetX, bestTargetY + translateTargetY);
    }

    public void clear() {
        this.bestTargetFidId = -1;
        this.bestTargetDistance = -1;
        this.bestTargetAmbiguity = -1;
        this.bestTargetX = -1;
        this.bestTargetY = -1;
        this.bestTargetZ = -1;
        this.bestTargetAge = -1;
        this.bestTargetYawInDeg = -1;
        this.lastEstGlobalPose = null;
    }    

    public void ageOutResults(String lastReason) {
      if (Timer.getFPGATimestamp() - bestTargetAge > 0.1) {
        clear();
        this.lastReason = lastReason;
      }
    }

    @Override
    public SendableFluent addSendables(SendableFluent s) {
      System.out.println("*******************************");
      System.out.println(s.getCurrentKey());
      System.out.println("*******************************");
      s.addDefaultKey("apr-tag-info");
      if (s.debugMode) {
          s.addInteger("bestTargetFidId", () -> bestTargetFidId, null);
          s.addDouble("bestTargetDistance", () -> Units.metersToInches(bestTargetDistance), null);
          s.addDouble("bestTargetAmbiguity", () -> bestTargetAmbiguity, null);
          s.addDouble("bestTargetX", () -> Units.metersToInches(bestTargetX), null);
          s.addDouble("bestTargetY", () -> Units.metersToInches(bestTargetY), null);          
          s.addDouble("bestTargetZ", () -> Units.metersToInches(bestTargetZ), null);          
          s.addDouble("bestTargetYawInDeg", () -> bestTargetYawInDeg, null);          
          //s.addString("lastEstGlobalPose", () -> lastEstGlobalPose == null ? "<null>" : lastEstGlobalPose.toString(), null);          
          s.addDouble("lastEstGlobalPose.X", () -> lastEstGlobalPose == null ? -1 : Units.metersToInches(lastEstGlobalPose.getX()), null);          
          s.addDouble("lastEstGlobalPose.Y", () -> lastEstGlobalPose == null ? -1 : Units.metersToInches(lastEstGlobalPose.getY()), null);          
          s.addDouble("lastEstGlobalPose.Z", () -> lastEstGlobalPose == null ? -1 : Units.metersToInches(lastEstGlobalPose.getZ()), null);          
          s.addDouble("lastEstGlobalPose.Rot", () -> lastEstGlobalPose == null ? -1 : lastEstGlobalPose.getRotation().toRotation2d().getDegrees(), null);
          s.addString("lastReason", () -> lastReason, null);          
      }             
      s.removeDefaultKey();                
      return s;       
    }

}
