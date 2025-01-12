package org.aa8426.examples.odometry;

import edu.wpi.first.apriltag.AprilTagFieldLayout;
import edu.wpi.first.apriltag.AprilTagFields;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;

/**
 * Does basic robot-centric movement of a pose using CCW positive measurements. 
 * 
 * Some aspect of this exists in Transform
 */
public class OdometryHelper {
    
    Pose2d pose2d;
    static final AprilTagFieldLayout fieldLayout = AprilTagFieldLayout.loadField(AprilTagFields.k2025Reefscape);

    public OdometryHelper(Pose2d pose2d) {
        this.pose2d = pose2d;        
    }    

    public OdometryHelper move(double distance, Rotation2d angle) {        
        double xNew = pose2d.getX() + distance * Math.cos(angle.getRadians());
        double yNew = pose2d.getY() + distance * Math.sin(angle.getRadians());
        
        this.pose2d = new Pose2d(xNew, yNew, pose2d.getRotation());        
        return this;
    }

    public OdometryHelper flip() {                
        this.pose2d = new Pose2d(pose2d.getX(), pose2d.getY(), Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()+180));
        return this;
    }

    public OdometryHelper moveForward(double distance) {
        move(distance, Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()+0));
        return this;
    }

    public OdometryHelper moveRight(double distance) {
        move(distance, Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()-90));
        return this;
    }

    public OdometryHelper moveleft(double distance) {
        move(distance, Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()+90));
        return this;
    }

    public OdometryHelper moveBackward(double distance) {
        move(distance, Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()+180));
        return this;
    }

    public OdometryHelper face(Pose2d pose2d) {
        this.pose2d = new Pose2d(this.pose2d.getX(), this.pose2d.getY(), Rotation2d.fromDegrees(getAngle(pose2d, this.pose2d)));
        return this;
    }

    private double getAngle(Pose2d p1, Pose2d p2) {
        return getAngle(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    private double getAngle(double x1, double y1, double x2, double y2) {
        double dy = y2 - y1;
        double dx = x2 - x1;
        double theta = Math.atan2(dy, dx);
        theta = theta * (180 / Math.PI);
        theta = theta + 180;
        return theta;
    }

    public Pose2d getPose2d() {
        return this.pose2d;
    }

    public OdometryHelper setPose2d(Pose2d pose2d) {
        this.pose2d = pose2d;
        return this;
    }

    public Pose2d get() {
        return this.pose2d;
    }

    public OdometryHelper mirrorPoseForAlliance() {
        if (this.pose2d.getX() > (fieldLayout.getFieldLength() / 2)) {
            this.pose2d = new Pose2d(Math.abs(this.pose2d.getX() - fieldLayout.getFieldLength()), this.pose2d.getY(), this.pose2d.getRotation());
        } else {
            this.pose2d = new Pose2d(fieldLayout.getFieldLength() - this.pose2d.getX(), this.pose2d.getY(), this.pose2d.getRotation());
        }
        SwerveDriveKinematics sdk;        
        return this;
    }    

    public static void main(String[] args) {
        OdometryHelper oh = new OdometryHelper(new Pose2d(0, 0, Rotation2d.fromDegrees(45)));
        oh.move(5,Rotation2d.fromDegrees(53.13));
        System.out.println(oh.get());

        oh = new OdometryHelper(new Pose2d(0, 0, Rotation2d.fromDegrees(90)));
        oh.moveRight(4)
          .moveForward(3);
        System.out.println(oh.get());

        oh = new OdometryHelper(new Pose2d(0, 0, Rotation2d.fromDegrees(0)));
        oh.face(new Pose2d(4, 3, Rotation2d.fromDegrees(0)));
        System.out.println(oh.get());
    }
}
