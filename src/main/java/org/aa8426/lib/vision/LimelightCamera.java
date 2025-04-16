package frc.aa8426.utils.vision;

import java.util.function.Supplier;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Timer;
import frc.aa8426.subsystems.SwerveSubsystem;
import frc.aa8426.utils.Odometry.VisionMeasurement;
import frc.aa8426.utils.dashboard.SendableFluent;
import frc.aa8426.utils.dashboard.SendableFluent.ISendableFluent;
import frc.aa8426.utils.vision.LimelightHelpers.RawFiducial;

public class LimelightCamera implements ISendableFluent{

    private SwerveSubsystem swerveSubsystem;
    private String limelightName;
    private VisionSingleTargetInfo singleTargetInfo = new VisionSingleTargetInfo();
    
    public LimelightCamera(String limelightName, SwerveSubsystem swerveSubsystem) {
        this.swerveSubsystem = swerveSubsystem;
        this.limelightName = limelightName;
        addSendables(SendableFluent.getInstance());
    }
    
    public Pose2d getLatestPose() {
        if (singleTargetInfo.lastEstGlobalPose == null) {
            return null;
          } 
        return singleTargetInfo.lastEstGlobalPose.toPose2d();
    }

    public VisionMeasurement getVisionMeasurement() {
        return new VisionMeasurement(
            new Pose2d(singleTargetInfo.bestTargetX, singleTargetInfo.bestTargetY, Rotation2d.fromDegrees(singleTargetInfo.bestTargetYawInDeg)),
            singleTargetInfo.bestTargetAge            
        );
    }

    public Supplier<Pose2d> getLatestPoseSupplier() {
        return () -> singleTargetInfo.lastEstGlobalPose.toPose2d();
    }

    public void updateOnlyBlue() {
        Pose3d pose = LimelightHelpers.getBotPose3d_wpiBlue(limelightName); // is recommended!
        if (pose.getX() == 0.0) {
            return;
        }
        
        singleTargetInfo.lastEstGlobalPose = pose;        
        // if (swerveSubsystem.resetWithVision) {
        //     poseSet = pose.toPose2d();
        //     System.out.println("using full pose");
        //     // resetWithVision = false;
        // } else {
        //     poseSet = new Pose2d(pose.getX(), pose.getY(), swerveSubsystem.getHeading());
        // }
        swerveSubsystem.swerveDrive.addVisionMeasurement(singleTargetInfo.lastEstGlobalPose.toPose2d(), singleTargetInfo.bestTargetAge,
                VecBuilder.fill(.7, .7, Rotation2d.fromDegrees(0).getRadians()));
    }

    public void updateWithMegaTag2AndWPIBlue() {

        LimelightHelpers.SetRobotOrientation(limelightName, swerveSubsystem.getHeading().getRadians() + Math.PI, 0, 0, 0, 0, 0);
        LimelightHelpers.PoseEstimate mt2 = LimelightHelpers.getBotPoseEstimate_wpiBlue_MegaTag2("limelight");
                
        if (mt2 == null) {
            singleTargetInfo.ageOutResults("mt2 was null");
            return; // "reject update"
        }
        if (Math.abs(swerveSubsystem.swerveDrive.getGyro().getYawAngularVelocity().baseUnitMagnitude()) > 720) {
            singleTargetInfo.ageOutResults("ang vel");
            return; // "reject update"
        }
        if (mt2.tagCount == 0) {
            singleTargetInfo.ageOutResults("tag count 0");
            return; // "reject update"
        }
        
        RawFiducial bestFid = null;
        String fidFail = "count="+mt2.rawFiducials.length+";";
        String delim = "";
        for (int x=0;x<mt2.rawFiducials.length;x++) {
            RawFiducial fid = mt2.rawFiducials[x];
            if (fid.distToCamera > 3) {                
                fidFail = delim+x+":distFail";
                delim = ",";
                continue;
            }
            if (fid.ambiguity > .5) {                
                fidFail = delim+x+":ambigFail";
                delim = ",";
                continue;
            }
            if (bestFid == null || fid.distToCamera < bestFid.distToCamera) {
                bestFid = fid;
            }
        }
        if (bestFid == null) {
            singleTargetInfo.ageOutResults("no best fid:"+fidFail);
            return; // "reject update"
        }
        
        singleTargetInfo.bestTargetFidId = bestFid.id;
        singleTargetInfo.bestTargetDistance = bestFid.distToCamera;
        singleTargetInfo.bestTargetAmbiguity = bestFid.ambiguity;        
        Pose3d cameraPose3d_TargetSpace = LimelightHelpers.getCameraPose3d_TargetSpace(limelightName);
        singleTargetInfo.bestTargetX = cameraPose3d_TargetSpace.getX();
        singleTargetInfo.bestTargetY = cameraPose3d_TargetSpace.getY();
        singleTargetInfo.bestTargetZ = cameraPose3d_TargetSpace.getZ();
        singleTargetInfo.bestTargetYawInDeg = cameraPose3d_TargetSpace.getRotation().toRotation2d().getDegrees();
        singleTargetInfo.bestTargetAge = Timer.getFPGATimestamp();                

        updateOnlyBlue(); // we then go grab the actual estimate global position from another method.
    }

    @Override
    public SendableFluent addSendables(SendableFluent s) {
      s.addDefaultKey("limelight-"+limelightName);
      if (s.debugMode) {
          singleTargetInfo.addSendables(s);
      }             
      s.removeDefaultKey();                
      return s;       
    }

}
