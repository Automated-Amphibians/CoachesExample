package frc.aa8426.utils.vision;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.photonvision.EstimatedRobotPose;
import org.photonvision.PhotonCamera;
import org.photonvision.PhotonPoseEstimator;
import org.photonvision.PhotonPoseEstimator.PoseStrategy;
import org.photonvision.simulation.PhotonCameraSim;
import org.photonvision.simulation.SimCameraProperties;
import org.photonvision.simulation.VisionSystemSim;
import org.photonvision.targeting.PhotonPipelineResult;
import org.photonvision.targeting.PhotonTrackedTarget;

import edu.wpi.first.math.Matrix;
import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.numbers.N1;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;
import frc.aa8426.Robot;
import frc.aa8426.utils.dashboard.SendableFluent;
import frc.aa8426.utils.dashboard.SendableFluent.ISendableFluent;

/**
 * This class was once part of Vision.java, but was removed to emphasize that this is the the
 * relevant class to change to make vision work.
 * 
 * Part of the Example PhotonVision class to aid in the pursuit of accurate odometry. Taken from
 * https://gitlab.com/ironclad_code/ironclad-2024/-/blob/master/src/main/java/frc/robot/vision/Vision.java?ref_type=heads
 * 
 */
public enum PhotonVisionCameras implements ISendableFluent {
    /**
     * Left Camera
     */
    // FRONT_HIGH_CAM("FrontHighCam",
    //     new Rotation3d(0, Math.toRadians(30) /* up is positive */, Math.toRadians(0)), // facing left would be 30
    //     new Translation3d(Units.inchesToMeters(7.75), // forward->backward
    //         Units.inchesToMeters(-9), // left->right (left)
    //         Units.inchesToMeters(24)), // up
    //     VecBuilder.fill(4, 4, 8), VecBuilder.fill(0.5, 0.5, 1)),
    /**
     * Right Camera
     */
    // BACK_RIGHT_CAM("BackRightCam",
    //     new Rotation3d(0, Math.toRadians(0) /* up/down */, Math.toRadians(-180)), // facing right would be -30
    //     new Translation3d(Units.inchesToMeters(-12.75), // forward->backward
    //         Units.inchesToMeters(-9.5), // left->right (right)
    //         Units.inchesToMeters(9.34)), // up
    //     VecBuilder.fill(4, 4, 8), VecBuilder.fill(0.5, 0.5, 1));
    BACK_RIGHT_CAM("BackRightCam",
        new Rotation3d(0, Math.toRadians(0) /* up/down */, Math.toRadians(0)), // facing right would be -30
        new Translation3d(Units.inchesToMeters(12.75), // forward->backward
            Units.inchesToMeters(0), // left->right (right)
            Units.inchesToMeters(9.34)), // up
        VecBuilder.fill(4, 4, 8), VecBuilder.fill(0.5, 0.5, 1));

    /**
     * Latency alert to use when high latency is detected.
     */
    public final Alert latencyAlert;
    /**
     * Camera instance for comms.
     */
    public final PhotonCamera camera;
    /**
     * Pose estimator for camera.
     */
    public final PhotonPoseEstimator poseEstimator;
    /**
     * Standard Deviation for single tag readings for pose estimation.
     */
    private final Matrix<N3, N1> singleTagStdDevs;
    /**
     * Standard deviation for multi-tag readings for pose estimation.
     */
    private final Matrix<N3, N1> multiTagStdDevs;
    /**
     * Transform of the camera rotation and translation relative to the center of
     * the robot
     */
    public final Transform3d robotToCamTransform;
    /**
     * Current standard deviations used.
     */
    public Matrix<N3, N1> curStdDevs;
    /**
     * Estimated robot pose.
     */
    public Optional<EstimatedRobotPose> estimatedRobotPose;
    /**
     * Simulated camera instance which only exists during simulations.
     */
    public PhotonCameraSim cameraSim;
    /**
     * Results list to be updated periodically and cached to avoid unnecessary
     * queries.
     */
    public List<PhotonPipelineResult> resultsList = new ArrayList<>();


    VisionSingleTargetInfo singleTargetInfo = new VisionSingleTargetInfo();
    
    /**
     * Last read from the camera timestamp to prevent lag due to slow data fetches.
     */
    //private double lastReadTimestamp = Microseconds.of(NetworkTablesJNI.now()).in(Seconds);

    /**
     * Construct a Photon Camera class with help. Standard deviations are fake
     * values, experiment and determine
     * estimation noise on an actual robot.
     *
     * @param name                  Name of the PhotonVision camera found in the PV
     *                              UI.
     * @param robotToCamRotation    {@link Rotation3d} of the camera.
     * @param robotToCamTranslation {@link Translation3d} relative to the center of
     *                              the robot.
     * @param singleTagStdDevs      Single AprilTag standard deviations of estimated
     *                              poses from the camera.
     * @param multiTagStdDevsMatrix Multi AprilTag standard deviations of estimated
     *                              poses from the camera.
     */
    PhotonVisionCameras(String name, Rotation3d robotToCamRotation, Translation3d robotToCamTranslation,
        Matrix<N3, N1> singleTagStdDevs, Matrix<N3, N1> multiTagStdDevsMatrix) {
      latencyAlert = new Alert("'" + name + "' Camera is experiencing high latency.", AlertType.kWarning);

      camera = new PhotonCamera(name);

      // https://docs.wpilib.org/en/stable/docs/software/basic-programming/coordinate-system.html
      robotToCamTransform = new Transform3d(robotToCamTranslation, robotToCamRotation);

      poseEstimator = new PhotonPoseEstimator(PhotonVision.fieldLayout,
          PoseStrategy.MULTI_TAG_PNP_ON_COPROCESSOR,
          robotToCamTransform);
      poseEstimator.setMultiTagFallbackStrategy(PoseStrategy.LOWEST_AMBIGUITY);

      this.singleTagStdDevs = singleTagStdDevs;
      this.multiTagStdDevs = multiTagStdDevsMatrix;

      if (Robot.isSimulation()) {
        SimCameraProperties cameraProp = new SimCameraProperties();
        // A 640 x 480 camera with a 100 degree diagonal FOV.
        cameraProp.setCalibration(960, 720, Rotation2d.fromDegrees(100));
        // Approximate detection noise with average and standard deviation error in
        // pixels.
        cameraProp.setCalibError(0.25, 0.08);
        // Set the camera image capture framerate (Note: this is limited by robot loop
        // rate).
        cameraProp.setFPS(30);
        // The average and standard deviation in milliseconds of image data latency.
        cameraProp.setAvgLatencyMs(35);
        cameraProp.setLatencyStdDevMs(5);

        cameraSim = new PhotonCameraSim(camera, cameraProp);
        cameraSim.enableDrawWireframe(true);
      }

      addSendables(SendableFluent.getInstance());
    }

    /**
     * Add camera to {@link VisionSystemSim} for simulated photon vision.
     *
     * @param systemSim {@link VisionSystemSim} to use.
     */
    public void addToVisionSim(VisionSystemSim systemSim) {
      if (Robot.isSimulation()) {
        systemSim.addCamera(cameraSim, robotToCamTransform);
      }
    }

    /**
     * Get the result with the least ambiguity from the best tracked target within
     * the Cache. This may not be the most
     * recent result!
     *
     * @return The result in the cache with the least ambiguous best tracked target.
     *         This is not the most recent result!
     */
    public Optional<PhotonPipelineResult> getBestResult() {
      if (resultsList.isEmpty()) {
        return Optional.empty();
      }

      PhotonPipelineResult bestResult = resultsList.get(0);
      double amiguity = bestResult.getBestTarget().getPoseAmbiguity();
      double currentAmbiguity = 0;
      for (PhotonPipelineResult result : resultsList) {
        currentAmbiguity = result.getBestTarget().getPoseAmbiguity();
        if (currentAmbiguity < amiguity && currentAmbiguity > 0) {
          bestResult = result;
          amiguity = currentAmbiguity;
        }
      }
      return Optional.of(bestResult);
    }

    /**
     * Get the latest result from the current cache.
     *
     * @return Empty optional if nothing is found. Latest result if something is
     *         there.
     */
    public Optional<PhotonPipelineResult> getLatestResult() {
      return resultsList.isEmpty() ? Optional.empty() : Optional.of(resultsList.get(0));
    }

    /**
     * Get the estimated robot pose. Updates the current robot pose estimation,
     * standard deviations, and flushes the
     * cache of results.
     *
     * @return Estimated pose.
     */
    public Optional<EstimatedRobotPose> getEstimatedGlobalPose() {
      updateUnreadResults();
      return estimatedRobotPose;
    }    

    /**
     * Same as getEstimatedGlobalPose, but does use or a cache of results. As long as you
     * aren't calling this method more than once per loop, this should be fine.
     * 
     * @return
     */
    public Optional<EstimatedRobotPose> getEstimatedGlobalPoseSimplified() {
      resultsList = Robot.isReal() ? camera.getAllUnreadResults() : cameraSim.getCamera().getAllUnreadResults();
      double maxDist = 9.0;
      double maximumAmbiguity = 0.2;
      estimatedRobotPose = Optional.empty();
      if (resultsList.isEmpty()) {        
        singleTargetInfo.ageOutResults("resultEmpty");
        return estimatedRobotPose;
      }
      Optional<EstimatedRobotPose> visionEst = Optional.empty();
      boolean foundTarget = false;
      for (PhotonPipelineResult change : resultsList) {
        if (!change.hasTargets()) {
          continue;
        }
        PhotonTrackedTarget bestTarget = change.getBestTarget();
        if (bestTarget == null) {          
          continue;
        }        
        
        if (!foundTarget) { // only update for one target, the best should be the first anyway
          singleTargetInfo.bestTargetDistance = bestTarget.bestCameraToTarget.getTranslation().getNorm();
          singleTargetInfo.bestTargetAmbiguity = bestTarget.poseAmbiguity;
          singleTargetInfo.bestTargetFidId = bestTarget.fiducialId;
          singleTargetInfo.bestTargetX = bestTarget.bestCameraToTarget.getTranslation().getX(); // fwd/back
          singleTargetInfo.bestTargetY = bestTarget.bestCameraToTarget.getTranslation().getY(); // lft/right
          singleTargetInfo.bestTargetZ = bestTarget.bestCameraToTarget.getTranslation().getZ(); // lft/right
          singleTargetInfo.bestTargetYawInDeg = bestTarget.bestCameraToTarget.getRotation().toRotation2d().getDegrees();
          singleTargetInfo.bestTargetAge = change.getTimestampSeconds();
          foundTarget = true;
        }

        if (singleTargetInfo.bestTargetAmbiguity > maximumAmbiguity) {          
          return Optional.empty();
        }        
        // if (singleTargetInfo.bestTargetDistance > maxDist) {
        //   return Optional.empty();
        // }
        

        estimatedRobotPose = poseEstimator.update(change);                
        if (estimatedRobotPose.isPresent()) {
          singleTargetInfo.lastEstGlobalPose = estimatedRobotPose.get().estimatedPose;
        }
        updateEstimationStdDevs(visionEst, change.getTargets());
      }
      if (!foundTarget) {
        singleTargetInfo.ageOutResults("no target found");
      }
      //estimatedRobotPose = visionEst;
      return estimatedRobotPose;
    }

    /**
     * Update the latest results, cached with a maximum refresh rate of 1req/15ms.
     * Sorts the list by timestamp.
     */
    private void updateUnreadResults() {
      //double mostRecentTimestamp = resultsList.isEmpty() ? 0.0 : resultsList.get(0).getTimestampSeconds();
      //double currentTimestamp = Microseconds.of(NetworkTablesJNI.now()).in(Seconds);
      //double debounceTime = Milliseconds.of(15).in(Seconds);
      //for (PhotonPipelineResult result : resultsList) {
      //  mostRecentTimestamp = Math.max(mostRecentTimestamp, result.getTimestampSeconds());
      //}
      //System.out.println((currentTimestamp - mostRecentTimestamp >= debounceTime));
      // if ((resultsList.isEmpty() || (currentTimestamp - mostRecentTimestamp >= debounceTime)) &&
      //     (currentTimestamp - lastReadTimestamp) >= debounceTime) {
        resultsList = Robot.isReal() ? camera.getAllUnreadResults() : cameraSim.getCamera().getAllUnreadResults();
        //lastReadTimestamp = currentTimestamp;
        // resultsList.sort((PhotonPipelineResult a, PhotonPipelineResult b) -> {
        //   return a.getTimestampSeconds() >= b.getTimestampSeconds() ? 1 : -1;
        // });
        //System.out.println("Hello!");
        if (!resultsList.isEmpty()) {
          updateEstimatedGlobalPose();
        }
      //}
    }

    /**
     * The latest estimated robot pose on the field from vision data. This may be
     * empty. This should only be called once
     * per loop.
     *
     * <p>
     * Also includes updates for the standard deviations, which can (optionally) be
     * retrieved with
     * {@link PhotonVisionCameras#updateEstimationStdDevs}
     *
     * @return An {@link EstimatedRobotPose} with an estimated pose, estimate
     *         timestamp, and targets used for
     *         estimation.
     */
    private void updateEstimatedGlobalPose() {
      Optional<EstimatedRobotPose> visionEst = Optional.empty();
      for (var change : resultsList) {
        visionEst = poseEstimator.update(change);
        updateEstimationStdDevs(visionEst, change.getTargets());
      }
      estimatedRobotPose = visionEst;
    }

    /**
     * Calculates new standard deviations This algorithm is a heuristic that creates
     * dynamic standard deviations based
     * on number of tags, estimation strategy, and distance from the tags.
     *
     * @param estimatedPose The estimated pose to guess standard deviations for.
     * @param targets       All targets in this camera frame
     */
    private void updateEstimationStdDevs(
        Optional<EstimatedRobotPose> estimatedPose, List<PhotonTrackedTarget> targets) {
      if (estimatedPose.isEmpty()) {
        // No pose input. Default to single-tag std devs
        curStdDevs = singleTagStdDevs;

      } else {
        // Pose present. Start running Heuristic
        var estStdDevs = singleTagStdDevs;
        int numTags = 0;
        double avgDist = 0;

        // Precalculation - see how many tags we found, and calculate an
        // average-distance metric
        for (var tgt : targets) {
          var tagPose = poseEstimator.getFieldTags().getTagPose(tgt.getFiducialId());
          if (tagPose.isEmpty()) {
            continue;
          }
          numTags++;
          avgDist += tagPose
              .get()
              .toPose2d()
              .getTranslation()
              .getDistance(estimatedPose.get().estimatedPose.toPose2d().getTranslation());
        }

        if (numTags == 0) {
          // No tags visible. Default to single-tag std devs
          curStdDevs = singleTagStdDevs;
        } else {
          // One or more tags visible, run the full heuristic.
          avgDist /= numTags;
          // Decrease std devs if multiple targets are visible
          if (numTags > 1) {
            estStdDevs = multiTagStdDevs;
          }
          // Increase std devs based on (average) distance
          if (numTags == 1 && avgDist > 4) {
            estStdDevs = VecBuilder.fill(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
          } else {
            estStdDevs = estStdDevs.times(1 + (avgDist * avgDist / 30));
          }
          curStdDevs = estStdDevs;
        }
      }
    }

    public Pose2d getLatestPose() {
        if (singleTargetInfo.lastEstGlobalPose == null) {
          return null;
        } 
        return singleTargetInfo.lastEstGlobalPose.toPose2d();
    }
    
    @Override
    public SendableFluent addSendables(SendableFluent s) {
      s.addDefaultKey("photonvision-"+name());
      if (s.debugMode) {
          singleTargetInfo.addSendables(s);
      }             
      s.removeDefaultKey();                
      return s;       
    }

}
