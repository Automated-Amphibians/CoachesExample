package org.aa8426.lib.vision;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.aa8426.lib.odometry.Reefscape;
import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.Pair;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.util.Units;
import swervelib.SwerveDrive;

/** 
 * Originally modeled after:
 * 
 * https://github.com/FRCTeam1672/2025-Robot/blob/0ffe8eb52a1427f8c7356f902a5202ee6b53c507/src/main/java/frc/robot/subsystems/swervedrive/VisionSubsystem.java
 * 
 * Need to look at:
 * https://github.com/FRC2832/Robot2832-2025Njord/blob/2c2e340a41bc627b573a1f7fbed835c77bccbe9f/src/main/java/frc/robot/vision/AprilTagCamera.java
 * 
 * This class is currently unused, odometry is handled by Cameras+PhotonVision (for PhotonVision) and LimelightCamera (for Limelight)
 */
public class VisionSubsystem  {    

    List<Pair<PhotonCamera, PhotonPoseEstimator>> cameras = new ArrayList<>();
    // https://github.com/TheTriSonics/Reefscape2025/blob/fae9af63fbf3a254a9ab589c0374ba8a57c96a00/components/vision.py#L61
    //     if tag_dist < 2.0 and tag_id in taget_ids_in_frame:
    //     # We're close to an apriltag, so we should use that for
    //     # vision. We'll tighten up the std devs to make sure we
    //     # are trusting this reading.
    //     self.std_x, self.std_y, self.std_rot = 0.1, 0.1, radians(22.5)
    // else:
    //     self.std_x, self.std_y, self.std_rot = 0.4, 0.4, radians(45)
    private Vector<N3> highConfidenceStdDevs = VecBuilder.fill(.1,.1,Rotation2d.fromDegrees(22.5).getRadians());
    //private Vector<N3> lowerConfidenceStdDevs = VecBuilder.fill(.4,.4,Rotation2d.fromDegrees(45).getRadians());

    /** Creates a new VisionSubsystem. */
    public VisionSubsystem() {
        Transform3d frontCamPos = new Transform3d(new Translation3d(Units.inchesToMeters(6),
            Units.inchesToMeters(-11),
            Units.inchesToMeters(25)),
            new Rotation3d(0, Math.toRadians(0), Math.toRadians(3)));

        Transform3d backCamPos = new Transform3d(new Translation3d(Units.inchesToMeters(4),
                Units.inchesToMeters(-11),
                Units.inchesToMeters(25)),
                new Rotation3d(0, Math.toRadians(0), Math.toRadians(180))); // Cam mounted facing forward, half a meter

        addCamera("1672_Camera1", "front", frontCamPos);        
        addCamera("1672_Camera2", "back", backCamPos);        
    }    

    private void addCamera(String name, String niceName, Transform3d cameraLoc) {
        PhotonCamera camera = new PhotonCamera(name);        
        PhotonPoseEstimator poseEst = new PhotonPoseEstimator(Reefscape.fieldLayout, PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR, cameraLoc); 
        //PoseStrategy.LOWEST_AMBIGUITY
        // backCamTrigger.onFalse(Commands.runOnce(() -> {
        //     Elastic.sendNotification(
        //         new Notification(Notification.NotificationLevel.ERROR, "Back Camera Disconnect", "Rear camera disconnect").withDisplaySeconds(5)
        //     );
        // }));
        cameras.add(new Pair<>(camera, poseEst));
    }

    /**
     * This method ignores updates from the camera if the target is too far away or too ambiguous before adding it to the post estimator.
     */
    private void updatePoseEstimationMethod2(SwerveDrive swerve, PhotonPoseEstimator postEst, List<PhotonPipelineResult> results) {
        if (results.isEmpty()) {
            return;
        }    
        
        for(PhotonPipelineResult result:results) {            
            if (!result.hasTargets()) {
                continue;
            }
                        
            PhotonTrackedTarget bestTarget = result.getBestTarget();                

            if ((bestTarget == null) || (bestTarget.poseAmbiguity > 0.4)) {
                continue; // skip this one, too ambiguous
            }
            if (bestTarget.bestCameraToTarget.getTranslation().getNorm() > 2) {
                continue; // skip this one, too far away
            }            

            Optional<EstimatedRobotPose> update =  postEst.update(result);
            if (update.isPresent()) {
                EstimatedRobotPose estimatedRobotPose = update.get();
                //swerve.swerveDrivePoseEstimator.setVisionMeasurementStdDevs(null);
                swerve.swerveDrivePoseEstimator.addVisionMeasurement(
                    estimatedRobotPose.estimatedPose.toPose2d(), 
                    result.getTimestampSeconds(),
                    highConfidenceStdDevs);
            }            
        }
    }

    // https://github.com/HuskieRobotics/3061-lib/blob/c34cf129871ce812f816327634fc94d6d15a0fdb/src/main/java/frc/lib/team3061/vision/VisionIOPhotonVision.java#L32
    /**
     * This method updates the pose estimator with the results from the camera first, and then ignores the
     */
    @SuppressWarnings("unused")
    private void updatePoseEstimation(SwerveDrive swerve, PhotonPoseEstimator postEst, List<PhotonPipelineResult> results) {
        if (results.isEmpty()) {
            return;
        }    
        
        for(PhotonPipelineResult result:results) {            
            Optional<EstimatedRobotPose> update = postEst.update(result);            
            
            if (update.isPresent()) {
                PhotonTrackedTarget bestTarget = result.getBestTarget();                

                if ((bestTarget == null) || (bestTarget.poseAmbiguity > 0.4)) {
                    continue; // skip this one, too ambiguous
                }
                if (bestTarget.bestCameraToTarget.getTranslation().getNorm() > 2) {
                    continue; // skip this one, too far away
                }
                                
                EstimatedRobotPose estimatedRobotPose = update.get();
                
                //swerve.swerveDrivePoseEstimator.setVisionMeasurementStdDevs(null);
                swerve.swerveDrivePoseEstimator.addVisionMeasurement(
                    estimatedRobotPose.estimatedPose.toPose2d(), 
                    result.getTimestampSeconds(),
                    highConfidenceStdDevs);
                
            }
        }
    }

    public void updatePoseEstimation(SwerveDrive swerve) {
        for(Pair<PhotonCamera, PhotonPoseEstimator> camera: cameras) {            
            updatePoseEstimationMethod2(swerve, camera.getSecond(), camera.getFirst().getAllUnreadResults());
        }
    }
}
