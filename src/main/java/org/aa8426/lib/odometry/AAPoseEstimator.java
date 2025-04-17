package org.aa8426.lib.odometry;

import java.util.function.Supplier;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import swervelib.SwerveDrive;

/**
 * A wrapper around SwervePoseEstimator with the following goals:
 * 
 * 1. Swapping what cameras are currently in use, resetting as appropriate
 * 2. Tweaking the weights/deviations is easier to insure as quick and accurate a response as possible
 * 3. Displaying results/error as individual points in AdvantageScope/glass  
 * 
 * This is currently not being used.
 */
public class AAPoseEstimator {
    SwerveDrivePoseEstimator swerveDrivePoseEstimator;
    SwerveDrive swerveDrive;
    Supplier<VisionMeasurement> vmSupplier;
    VisionMeasurement lastVisionMeasurement;

    public AAPoseEstimator(SwerveDrive swerveDrive, Pose2d startingPose, Supplier<VisionMeasurement> vmSupplier) {
        this.swerveDrive = swerveDrive;
        this.vmSupplier = vmSupplier;
        swerveDrivePoseEstimator =
                new SwerveDrivePoseEstimator(
                    swerveDrive.kinematics,
                    swerveDrive.getYaw(),
                    swerveDrive.getModulePositions(),
                    startingPose);        
                
    }

    /**
     * Performs a update cycle of the odometry/pose based on the inputs.
     */
    public void updatePoseEstimation() {
        swerveDrivePoseEstimator.update(swerveDrive.getYaw(), swerveDrive.getModulePositions());
        this.swerveDrive.field.setRobotPose(swerveDrivePoseEstimator.getEstimatedPosition());        
        this.lastVisionMeasurement = vmSupplier.get();
        if (lastVisionMeasurement.pose2d == null) {
            return;
        }        
        if (lastVisionMeasurement.stdDevs == null) {            
            swerveDrivePoseEstimator.addVisionMeasurement(lastVisionMeasurement.pose2d, lastVisionMeasurement.timestamp);
        } else {
            swerveDrivePoseEstimator.addVisionMeasurement(lastVisionMeasurement.pose2d, lastVisionMeasurement.timestamp, lastVisionMeasurement.stdDevs);
        }                
        this.swerveDrive.field.getObject("XModules").setPose(lastVisionMeasurement.pose2d);        
    }

    public Pose2d getPose() {
        return lastVisionMeasurement.pose2d;
    }

}
