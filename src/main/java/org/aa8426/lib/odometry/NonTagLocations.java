package org.aa8426.lib.odometry;

import org.aa8426.lib.odometry.Reefscape.AprilTagNames;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;


public enum NonTagLocations {
    STARTING_SPOT_C(7.25, 4, 180),
    STARTING_SPOT_L1(7.25, 5.08, 180),
    STARTING_SPOT_L2(7.25, 6.16, 180),
    STARTING_SPOT_L3(7.25, 7.24, 180),
    STARTING_SPOT_R1(7.25, 2.92, 180),
    STARTING_SPOT_R2(7.25, 1.84, 180),
    STARTING_SPOT_R3(7.25, 0.72, 180),
    CAGE_LEFT(8.8, 7.26, 180),
    CAGE_LEFT_CENTER(8.8, 6.705, 180),
    CAGE_CENTER(8.8, 6.16, 180),
    CAGE_RIGHT_CENTER(8.8, 5.62, 180),
    CAGE_RIGHT(8.8, 5.08, 180),
    LEFT_ALGAE(1.21, 5.85, 0),
    CENTER_ALGAE(1.21, 4, 0),
    RIGHT_ALGAE(1.21, 2.2, 0),
    LEFT_FRONT(new PoseHelp(
                    PoseHelp.getFacingAprilTag(AprilTagNames.BLUE_LEFT_FEEDER, 0.0))
                        .moveRight(0.3).get()),
    LEFT_CENTER(new PoseHelp(
        PoseHelp.getFacingAprilTag(AprilTagNames.BLUE_LEFT_FEEDER, 0.0))
            .get()),
    LEFT_BACK(new PoseHelp(
        PoseHelp.getFacingAprilTag(AprilTagNames.BLUE_LEFT_FEEDER, 0.0))
            .moveLeft(0.3).get()),
    RIGHT_FRONT(new PoseHelp(
        PoseHelp.getFacingAprilTag(AprilTagNames.BLUE_RIGHT_FEEDER, 0.0))
            .moveLeft(0.3).get()),
    RIGHT_CENTER(new PoseHelp(
        PoseHelp.getFacingAprilTag(AprilTagNames.BLUE_RIGHT_FEEDER, 0.0))
        .get()),
    RIGHT_BACK(new PoseHelp(
        PoseHelp.getFacingAprilTag(AprilTagNames.BLUE_RIGHT_FEEDER, 0.0))
            .moveRight(0.3).get())            
    ;

    private double x;
    private double y;
    private double angleInDegrees;
    
    NonTagLocations(double x, double y, double angleInDegrees) {
            this.x = x;
            this.y = y;
            this.angleInDegrees = angleInDegrees;
    }

    NonTagLocations(Pose2d pose) {
        this.x = pose.getX();
        this.y = pose.getY();
        this.angleInDegrees = pose.getRotation().getDegrees();
    }

    public Pose2d getAllianceCorrectPose2d() {
        return new PoseHelp(x, y, angleInDegrees).getAllianceCorrectPose2d();
    }

    public PoseHelp oh() {
        return new PoseHelp(new Pose2d(x, y, Rotation2d.fromDegrees(angleInDegrees)));
    }
    
}
