package org.aa8426.lib.Odometry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aa8426.lib.Utils;
import org.aa8426.lib.Odometry.Reefscape.AprilTagNames;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;

/**
 * Does basic robot-centric movement of a pose using CCW positive measurements. 
 * 
 * Some aspect of this exists in Transform
 */
public class PoseHelp {

    Pose2d startingPose;
    Pose2d pose2d;
    List<Pose2d> poses = new ArrayList<>();
    Map<String, Pose2d> poseMap = new HashMap<>();
    String defaultMapKey = null;    
    
    public PoseHelp() {
        this.startingPose = new Pose2d();
        this.pose2d = this.startingPose;
    }

    public PoseHelp(Pose2d pose2d) {
        //System.out.println(pose2d);
        this.startingPose = pose2d;
        this.pose2d = pose2d;                
    }

    public PoseHelp(double x, double y, double angleInDegrees) {
        this(new Pose2d(x, y, Rotation2d.fromDegrees(angleInDegrees)));
    }    

    public PoseHelp move(double distance, Rotation2d angle) {        
        double xNew = pose2d.getX() + distance * Math.cos(angle.getRadians());
        double yNew = pose2d.getY() + distance * Math.sin(angle.getRadians());        
        this.pose2d = new Pose2d(xNew, yNew, pose2d.getRotation());        
        return this;
    }

    public PoseHelp move(double x, double y, Rotation2d rot) {
        this.pose2d = this.pose2d.transformBy(new Transform2d(x, y, rot));
        return this;
    }

    public PoseHelp flip() {                
        this.pose2d = new Pose2d(pose2d.getX(), pose2d.getY(), Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()+180));        
        return this;
    }

    public PoseHelp rotateRight(int howfar) {                
        this.pose2d = new Pose2d(pose2d.getX(), pose2d.getY(), Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()-howfar));        
        return this;
    }

    public PoseHelp rotateLeft(int howfar) {                
        this.pose2d = new Pose2d(pose2d.getX(), pose2d.getY(), Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()+howfar));        
        return this;
    }

    public PoseHelp turnLeft90() {                
        this.pose2d = new Pose2d(pose2d.getX(), pose2d.getY(), Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()+90));
        return this;
    }

    public PoseHelp moveForward(double distance) {
        move(distance, Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()+0));
        return this;
    }

    public PoseHelp moveRight(double distance) {
        move(distance, Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()-90));
        return this;
    }

    public PoseHelp moveLeft(double distance) {
        move(distance, Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()+90));
        return this;
    }

    public PoseHelp moveBackward(double distance) {
        move(distance, Rotation2d.fromDegrees(pose2d.getRotation().getDegrees()+180));
        return this;
    }

    public PoseHelp face(Pose2d pose2d) {
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

    public PoseHelp addToMap(String key) {
        this.poseMap.put(key, pose2d);        
        return this;
    }

    public PoseHelp addToMap(String key, Pose2d pose) {
        this.pose2d = pose;
        addToMap(key);
        return this;
    }

    public PoseHelp setDefaultMapKey(String defaultMapKey) {
        this.defaultMapKey = defaultMapKey;
        return this;
    }

    public Pose2d getPoseFromMap(String key) {
        if (key == null) {
            return defaultMapKey == null ? null : poseMap.get(defaultMapKey);
        }
        if (this.poseMap.containsKey(key)) {
            return this.poseMap.get(key);
        }
        return defaultMapKey == null ? null : poseMap.get(defaultMapKey);        
    }

    public PoseHelp clearList() {
        this.poses.clear();
        return this;
    }

    public PoseHelp addToList() {
        this.poses.add(get());
        return this;
    }

    public List<Pose2d> getList() {
        return this.poses;
    }


    public Pose2d getPose2d() {
        return this.pose2d;
    }

    public PoseHelp setPose2d(Pose2d pose2d) {
        this.pose2d = pose2d;
        return this;
    }

    public Transform2d getTranslationFromStartingPositionFieldCentric() {
        return pose2d.minus(startingPose);
    }    

    public Pose2d get() {
        return this.pose2d;
    }

    public PoseHelp getAllianceCorrectPoseHelp() {
        if (isOnRedSide(this.pose2d)) {
            if (Utils.amRedAlliance()) {
                // do nothing
            } else {
                mirrorPoseForAlliance();
            }
        } else {
            if (Utils.amBlueAlliance()) {
                // do nothing
            } else {
                mirrorPoseForAlliance().get();
            }
        }
        return this;
    }

    public Pose2d getAllianceCorrectPose2d() {
        getAllianceCorrectPoseHelp();
        return this.get();
    }

    public Pose2d getEnemyAllianceCorrectPose2d() {
        if (isOnRedSide(this.pose2d)) {
            if (Utils.amRedAlliance()) {
                return mirrorPoseForAlliance().get();
            } else {                
                return this.pose2d;            
            }
        } else {
            if (Utils.amBlueAlliance()) {                
                return mirrorPoseForAlliance().get();
            } else {
                return this.pose2d;            
            }
        }
    }

    static public boolean isOnRedSide(Pose2d givenPose) {
        return (givenPose.getX() > (Reefscape.fieldLayout.getFieldLength() / 2));
    }

    static public boolean isOnBlueSide(Pose2d givenPose) {
        return !isOnBlueSide(givenPose);
    }    

    static public boolean isOnTopHalf(Pose2d givenPose) {
        return givenPose.getY() > (Reefscape.fieldLayout.getFieldWidth() / 2);
    }

    static public boolean isOnBottomHalf(Pose2d givenPose) {
        return !isOnTopHalf(givenPose);
    }

    static public Pose2d mirrorPose(Pose2d givenPose) {
        double x, y;
        Rotation2d rot;
        //boolean mirrorOnlyLength = false;
        if (isOnRedSide(givenPose)) {
            x = Math.abs(givenPose.getX() - Reefscape.fieldLayout.getFieldLength());
        } else {
            x = Reefscape.fieldLayout.getFieldLength() - givenPose.getX();
        }        
        if (isOnTopHalf(givenPose)) {
            y = Math.abs(givenPose.getY() - Reefscape.fieldLayout.getFieldWidth());
        } else {
            y = Reefscape.fieldLayout.getFieldWidth() - givenPose.getY();
        }
        /*if (mirrorOnlyLength) {
            if (this.pose2d.getRotation().getDegrees() < 180) {
                rot = Rotation2d.fromDegrees(180 - this.pose2d.getRotation().getDegrees());
            } else {
                rot = Rotation2d.fromDegrees((360 - this.pose2d.getRotation().getDegrees()) + 180);            
            }  
        } else {*/
            rot = Rotation2d.fromDegrees(givenPose.getRotation().getDegrees()+180);
        //}
        return new Pose2d(x, y, rot);
    }
    
    public PoseHelp mirrorPoseForAlliance() {
        this.pose2d = mirrorPose(this.pose2d);
        return this;
    }        

    public static Pose2d getFacingAprilTag(AprilTagNames tag, double robotOffset) {
        Pose2d pose2d = Reefscape.tags.get(tag.ordinal()).pose.toPose2d();
        PoseHelp oh = new PoseHelp(pose2d);
        oh.moveForward(robotOffset).flip();
        return oh.pose2d;
    }

    public double getDistance(Pose2d toPose) {
        return pose2d.getTranslation().getDistance(toPose.getTranslation());
    }

    static public double getDistance(Pose2d fromPose, AprilTagNames id) {
        return fromPose.getTranslation().getDistance(
            Reefscape.tags.get(id.ordinal()).pose.toPose2d().getTranslation()
        );
    }

    public double getDistanceToAprilTag(AprilTagNames id) {
        return getDistance(Reefscape.tags.get(id.ordinal()).pose.toPose2d());
    }

   
}
